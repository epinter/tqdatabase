/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

/*    This file is part of TQ Database.

    TQ Database is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TQ Database is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TQ Database.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.com.pinter.tqdatabase.data;

import br.com.pinter.tqdatabase.models.StorageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class ArcFile {
    private static final System.Logger logger = System.getLogger(ArcFile.class.getName());

    private final ByteBuffer arcBuffer;
    private final Map<String, ArcEntry> records;
    private final String arcFileName;

    ArcFile(String arcFileName) throws IOException {
        // Format of an ARC file
        // 0x08 - 4 bytes = // of files
        // 0x0C - 4 bytes = // of parts
        // 0x18 - 4 bytes = offset to directory structure
        //
        // Format of directory structure
        // 4-byte int = offset in file where this part begins
        // 4-byte int = size of compressed part
        // 4-byte int = size of uncompressed part
        // these triplets repeat for each part in the arc file
        // After these triplets are a bunch of null-terminated strings
        // which are the sub filenames.
        // After the subfilenames comes the subfile data:
        // 4-byte int = 3 == indicates start of subfile item  (maybe compressed flag??)
        //          1 == maybe uncompressed flag??
        // 4-byte int = offset in file where first part of this subfile begins
        // 4-byte int = compressed size of this file
        // 4-byte int = uncompressed size of this file
        // 4-byte crap
        // 4-byte crap
        // 4-byte crap
        // 4-byte int = numParts this file uses
        // 4-byte int = part// of first part for this file (starting at 0).
        // 4-byte int = length of filename string
        // 4-byte int = offset in directory structure for filename

        this.arcFileName = arcFileName;

        File file = new File(arcFileName);
        arcBuffer = ByteBuffer.allocate((Math.toIntExact(file.length()))).order(ByteOrder.LITTLE_ENDIAN);
        try (FileInputStream in = new FileInputStream(file)) {
            in.getChannel().read(arcBuffer);
        }
        arcBuffer.rewind();

        //test magic-number("ARC") and size
        byte[] magicNumber = new byte[3];
        arcBuffer.get(0, magicNumber);
        if (Arrays.compare(magicNumber, new byte[]{ 0x41, 0x52, 0x43}) != 0
                || arcBuffer.capacity() < 33) {
            throw new IOException("invalid file");
        }

        arcBuffer.position(8);

        int entries = arcBuffer.getInt();
        int numParts = arcBuffer.getInt();

        //skip two bytes
        arcBuffer.position(arcBuffer.position() + 8);

        int tocOffset = arcBuffer.getInt();

        if (arcBuffer.capacity() < (tocOffset + 12)) {
            throw new IOException("invalid file");
        }

        arcBuffer.position(tocOffset);

        logger.log(System.Logger.Level.TRACE, "entries:''{0}'' numParts:''{1}'' tocOffset:''{2}''", entries, numParts, tocOffset);

        Map<Integer, DataBlock> parts = new HashMap<>();
        records = new HashMap<>();

        for (int i = 0; i < numParts; i++) {
            int partFileOffset = arcBuffer.getInt();
            int partCompressedSize = arcBuffer.getInt();
            int partRealSize = arcBuffer.getInt();
            parts.put(i, new DataBlock(partFileOffset, partCompressedSize, partRealSize));
        }

        logger.log(System.Logger.Level.TRACE, "parts hashtable:''{0}''", parts.size());

        //filenames
        int filenamesOffset = arcBuffer.position();

        //skip filenames, 44 bytes each file data * number of entries
        arcBuffer.position(arcBuffer.capacity() - (44 * entries));

        for (int i = 0; i < entries; i++) {
            int storageTypeField = arcBuffer.getInt();
            int entryFileOffset = arcBuffer.getInt();
            int entryCompressedSize = arcBuffer.getInt();
            int entryRealSize = arcBuffer.getInt();

            StorageType entryStorageType;
            if(entryRealSize == entryCompressedSize && storageTypeField == 1) {
                entryStorageType = StorageType.UNCOMPRESSED;
            } else {
                entryStorageType = StorageType.COMPRESSED;
            }
            //skip 3 ints
            arcBuffer.position(arcBuffer.position() + 12);
            int nParts = arcBuffer.getInt();

            int firstPart = arcBuffer.getInt();

            byte[] filenameBytes = new byte[arcBuffer.getInt()];
            int entryNameOffset = arcBuffer.getInt();
            arcBuffer.get(filenamesOffset + entryNameOffset, filenameBytes);
            if (filenameBytes.length > 0) {
                ArcEntry entry = new ArcEntry(normalizeRecordPath(new String(filenameBytes, "CP1252")),
                        entryStorageType, entryFileOffset, entryCompressedSize, entryRealSize);
                logger.log(System.Logger.Level.TRACE, "record filename found ''{0}''({1}B length) - ''0x{2}''",
                        entry.getFilename(), filenameBytes.length, Integer.toHexString(filenamesOffset + entryNameOffset));

                if (entry.isCompressed()) {
                    for (int p = 0; p < nParts; p++) {
                        entry.addPart(p, parts.get(p + firstPart));
                    }
                }
                records.put(entry.getFilename(), entry);
            }
        }
        logger.log(System.Logger.Level.TRACE, "records hashtable:''{0}''", records.size());
    }

    public String getArcFileName() {
        return arcFileName;
    }

    List<String> listRecords() {
        return new ArrayList<>(records.keySet());
    }

    private String normalizeRecordPath(String recordId) {
        if (recordId == null || recordId.isEmpty()) {
            return null;
        }
        return recordId.toUpperCase().replace("/", "\\");
    }

    byte[] getData(String id) {
        if (id == null) return null;

        String dataId = id.replaceAll("[^\\\\]+\\\\(.*)", "$1");
        dataId = normalizeRecordPath(dataId);

        if (!records.containsKey(dataId) || dataId == null)
            return null;

        ArcEntry e = records.get(dataId);
        byte[] data = new byte[e.getRealSize()];
        if (e.getStorageType() == StorageType.UNCOMPRESSED) {
            arcBuffer.position(e.getFileOffset());
            arcBuffer.get(data, 0, e.getRealSize());
        } else {
            int pos = 0;
            for (DataBlock p : e.getParts()) {
                byte[] bufPart = decompressPart(p);
                logger.log(System.Logger.Level.TRACE, "reading... bufsz:''{0}'' pos:''{1}'' partcsz:''{2}''",
                        data.length, pos, p.realSize());
                System.arraycopy(bufPart, 0, data, pos, bufPart.length);
                pos += bufPart.length;
            }
        }

        return data;
    }

    private byte[] decompressPart(DataBlock part) {
        logger.log(System.Logger.Level.TRACE, "reading ''{0}'' bytes part from offset ''{1}''",
                part.compressedSize(), part.fileOffset());

        byte[] buffer = new byte[part.realSize()];
        Inflater inflater = new Inflater(true);
        try {
            inflater.setInput(arcBuffer.array(), part.fileOffset() + 2, part.compressedSize());

            inflater.inflate(buffer);
            inflater.end();

            logger.log(System.Logger.Level.TRACE, "buffer size ''{0}''", buffer.length);
        } catch (DataFormatException e) {
            logger.log(System.Logger.Level.ERROR, e);
        }
        return buffer;
    }
}
