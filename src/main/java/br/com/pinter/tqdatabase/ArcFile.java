/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class ArcFile {
    private final ByteBuffer arcBuffer;
    private Map<String, ArcEntry> records;
    private final System.Logger logger = Util.getLogger(ArcFile.class.getName());

    ArcFile(String fileName) throws IOException {
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

        File file = new File(fileName);
        arcBuffer = ByteBuffer.allocate((Math.toIntExact(file.length()))).order(ByteOrder.LITTLE_ENDIAN);
        try (FileChannel in = new FileInputStream(file).getChannel()) {
            in.read(arcBuffer);
        }
        arcBuffer.rewind();

        if (arcBuffer.get() != 0x41
                && arcBuffer.get() != 0x52
                && arcBuffer.get() != 0x43
                && arcBuffer.capacity() < 33) {
            throw new IOException("invalid file");
        }

        arcBuffer.position(8);

        int numEntries = arcBuffer.getInt();
        int numParts = arcBuffer.getInt();
        arcBuffer.position(24);

        int tocOffset = arcBuffer.getInt();

        if (arcBuffer.capacity() < (tocOffset + 12)) {
            throw new IOException("invalid file");
        }

        arcBuffer.position(tocOffset);

        logger.log(System.Logger.Level.TRACE, "numEntries:''{0}'' numPars:''{1}'' tocOffset:''{2}''", numEntries, numParts, tocOffset);

        Map<Integer, ArcPart> parts = new HashMap<>();
        Map<Integer, ArcEntry> entries = new HashMap<>();
        records = new HashMap<>();

        for (int i = 0; i < numParts; i++) {
            ArcPart part = new ArcPart();
            part.setFileOffset(arcBuffer.getInt());
            part.setCompressedSize(arcBuffer.getInt());
            part.setRealSize(arcBuffer.getInt());
            parts.put(i, part);
        }

        logger.log(System.Logger.Level.TRACE, "parts hashtable:''{0}''", parts.size());


        //filenames
        int filenamesOffset = arcBuffer.position();

        //file record offset from end of the file
        int fileRecordOffset = 44 * numEntries;

        arcBuffer.position(arcBuffer.capacity() - fileRecordOffset);

        for (int i = 0; i < numEntries; i++) {
            ArcEntry entry = new ArcEntry();
            entry.setStorageType(arcBuffer.getInt());
            entry.setFileOffset(arcBuffer.getInt());
            entry.setCompressedSize(arcBuffer.getInt());
            entry.setRealSize(arcBuffer.getInt());
            //skip 3 ints
            arcBuffer.position(arcBuffer.position() + 12);
            int nParts = arcBuffer.getInt();

            if (nParts >= 1) {
                entry.setParts(new ArrayList<>());
            }

            int firstPart = arcBuffer.getInt();

            //skip 2 ints - filename length and filename offset
            arcBuffer.position(arcBuffer.position() + 8);

            if (entry.getStorageType() != 1 && entry.isActive()) {
                for (int p = 0; p < nParts; p++) {
                    entry.getParts().add(p, parts.get(p + firstPart));
                }
            }
            entries.put(i, entry);
        }
        logger.log(System.Logger.Level.TRACE, "entries hashtable:''{0}''", entries.size());

        //record names
        arcBuffer.position(filenamesOffset);
        for (int i = 0; i < numEntries; i++) {
            ArcEntry entry = entries.get(i);
            StringBuilder str = new StringBuilder();

            if (entry.isActive()) {
                byte buf;
                for (; ; ) {
                    buf = arcBuffer.get();
                    if (buf == 0x00) {
                        break;
                    } else if (buf == 0x03) {
                        arcBuffer.position(arcBuffer.position() - 1);
                        buf = 0x00;
                        break;
                    }
                    str.append((char) buf);
                }
                String recordFilename = str.toString();
                if (!recordFilename.isEmpty()) {
                    records.put(normalizeRecordPath(recordFilename), entry);
                }
            }
        }

        logger.log(System.Logger.Level.TRACE, "records hashtable:''{0}''", records.size());

    }

    public List<String> listRecords() {
        return new ArrayList<>(records.keySet());
    }

    private String normalizeRecordPath(String recordId) {
        if (recordId == null || recordId.isEmpty()) {
            return null;
        }
        return recordId.toUpperCase().replace("/", "\\");
    }

    public byte[] getData(String id) {
        if (id == null) return null;

        String dataId = id.replaceAll("[^\\\\]+\\\\(.*)", "$1");
        dataId = normalizeRecordPath(dataId);

        if (!records.containsKey(dataId) || dataId == null)
            return null;

        ArcEntry e = records.get(dataId);
        byte[] data = new byte[e.getRealSize()];
        if (e.getStorageType() == 1 && e.getRealSize() == e.getCompressedSize()) {
            arcBuffer.position(e.getFileOffset());
            arcBuffer.get(data, 0, e.getRealSize());
        } else {
            int pos = 0;
            for (ArcPart p : e.getParts()) {
                byte[] bufPart = decompressPart(p);
                logger.log(System.Logger.Level.TRACE, "reading... bufsz:''{0}'' pos:''{1}'' partcsz:''{2}''",
                        data.length, pos, p.getRealSize());
                System.arraycopy(bufPart, 0, data, pos, bufPart.length);
                pos += bufPart.length;
            }
        }

        return data;
    }

    private byte[] decompressPart(ArcPart part) {
        logger.log(System.Logger.Level.TRACE, "reading ''{0}'' bytes part from offset ''{1}''",
                part.getCompressedSize(), part.getFileOffset());

        byte[] buffer = new byte[part.getRealSize()];
        Inflater inflater = new Inflater(true);
        try {
            inflater.setInput(arcBuffer.array(), part.getFileOffset() + 2, part.getCompressedSize());

            inflater.inflate(buffer);
            inflater.end();

            logger.log(System.Logger.Level.TRACE, "buffer size ''{0}''", buffer.length);
        } catch (DataFormatException e) {
            logger.log(System.Logger.Level.ERROR, e);
        }
        return buffer;
    }
}
