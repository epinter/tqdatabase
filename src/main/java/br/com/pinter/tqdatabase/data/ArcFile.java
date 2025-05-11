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

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;

class ArcFile {
    private static final System.Logger logger = System.getLogger(ArcFile.class.getName());
    private final Map<String, ArcEntry> records;
    private final Path arcFileName;

    ArcFile(Path arcFileName) throws IOException {
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

        try (RandomAccessFileLE arc = new RandomAccessFileLE(arcFileName.toString(), "r")) {
            //test magic-number("ARC") and size
            byte[] magicNumber = new byte[3];

            arc.readFully(magicNumber, 0, magicNumber.length);
            if (Arrays.compare(magicNumber, new byte[]{0x41, 0x52, 0x43}) != 0
                    || arc.length() < 33) {
                throw new IOException(String.format("Invalid file '%s' (length=%s)", arcFileName, arc.length()));
            }

            arc.seek(8);

            int entries = arc.readIntLE();
            int numParts = arc.readIntLE();

            //skip two bytes
            arc.seek(arc.getFilePointer() + 8);

            int tocOffset = arc.readIntLE();
            if (arc.length() < (tocOffset + 12)) {
                throw new IOException(
                        String.format("Invalid file '%s' (length=%s, tocOffset=%s)", arcFileName, arc.length(), (tocOffset + 12))
                );
            }

            arc.seek(tocOffset);
            logger.log(TRACE, "entries:''{0}'' numParts:''{1}'' tocOffset:''{2}''", entries, numParts, tocOffset);

            Map<Integer, DataBlock> parts = new HashMap<>();
            records = new HashMap<>();

            for (int i = 0; i < numParts; i++) {
                int partFileOffset = arc.readIntLE();
                int partCompressedSize = arc.readIntLE();
                int partRealSize = arc.readIntLE();
                parts.put(i, new DataBlock(partFileOffset, partCompressedSize, partRealSize));
            }

            logger.log(TRACE, "parts hashtable:''{0}''", parts.size());

            //filenames
            long filenamesOffset = arc.getFilePointer();

            //skip filenames, 44 bytes each file data * number of entries
            arc.seek(arc.length() - (44L * entries));

            for (int i = 0; i < entries; i++) {
                int storageTypeField = arc.readIntLE();
                int entryFileOffset = arc.readIntLE();
                int entryCompressedSize = arc.readIntLE();
                int entryRealSize = arc.readIntLE();

                StorageType entryStorageType;
                if (entryRealSize == entryCompressedSize && storageTypeField == 1) {
                    entryStorageType = StorageType.UNCOMPRESSED;
                } else {
                    entryStorageType = StorageType.COMPRESSED;
                }
                //skip 3 ints
                arc.seek(arc.getFilePointer() + 12);
                int nParts = arc.readIntLE();

                int firstPart = arc.readIntLE();

                byte[] filenameBytes = new byte[arc.readIntLE()];
                int entryNameOffset = arc.readIntLE();
                long prev = arc.getFilePointer();
                arc.seek(filenamesOffset + entryNameOffset);
                arc.readFully(filenameBytes, 0, filenameBytes.length);
                arc.seek(prev);

                if (filenameBytes.length > 0) {
                    ArcEntry entry = new ArcEntry(normalizeRecordPath(new String(filenameBytes, "CP1252")),
                            entryStorageType, entryFileOffset, entryCompressedSize, entryRealSize);
                    logger.log(TRACE, "record filename found ''{0}''({1}B length) - ''0x{2}''",
                            entry.getFilename(), filenameBytes.length, Long.toHexString(filenamesOffset + entryNameOffset));

                    if (entry.isCompressed()) {
                        for (int p = 0; p < nParts; p++) {
                            entry.addPart(p, parts.get(p + firstPart));
                        }
                    }
                    records.put(entry.getFilename(), entry);
                }
            }
        }

        logger.log(TRACE, "records hashtable:''{0}''", records.size());
    }

    public Path getArcFileName() {
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

        String dataId = id;
        dataId = normalizeRecordPath(dataId);

        if (!records.containsKey(dataId) || dataId == null)
            return null;
        ArcEntry e = records.get(dataId);
        byte[] data = new byte[e.getRealSize()];
        try (RandomAccessFileLE arc = new RandomAccessFileLE(arcFileName.toString(), "r")) {
            if (e.getStorageType() == StorageType.UNCOMPRESSED) {
                arc.get(e.getFileOffset(), data);
            } else {
                int pos = 0;
                for (DataBlock p : e.getParts()) {
                    byte[] bufPart = decompressPart(p);
                    logger.log(TRACE, "reading... bufsz:''{0}'' pos:''{1}'' partcsz:''{2}''",
                            data.length, pos, p.realSize());
                    System.arraycopy(bufPart, 0, data, pos, bufPart.length);
                    pos += bufPart.length;
                }
            }
        } catch (IOException ex) {
            logger.log(ERROR, "Error", ex);
        }
        return data;
    }

    private byte[] decompressPart(DataBlock part) {
        logger.log(TRACE, "reading ''{0}'' bytes part from offset ''{1}''",
                part.compressedSize(), part.fileOffset());

        byte[] buffer = new byte[part.realSize()];
        Inflater inflater = new Inflater(true);
        try (RandomAccessFileLE arc = new RandomAccessFileLE(arcFileName.toString(), "r")) {
            try {
                byte[] input = new byte[part.compressedSize()];
                arc.get(part.fileOffset() + 2, input);
                inflater.setInput(input);
                inflater.inflate(buffer);
                inflater.end();

                logger.log(TRACE, "buffer size ''{0}''", buffer.length);
            } catch (DataFormatException e) {
                logger.log(ERROR, "Error", e);
            }
        } catch (IOException ex) {
            logger.log(ERROR, ex);
        }
        return buffer;
    }
}
