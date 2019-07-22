/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.models.DbVariable;
import br.com.pinter.tqdatabase.util.Util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Hashtable;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class ArzFile {
    private final ByteBuffer arzBuffer;
    private String[] stringsTable;
    private Hashtable<String, DbRecord> recordsMetadata;
    private boolean useCache;
    private final System.Logger logger = Util.getLogger(ArzFile.class.getName());

    @SuppressWarnings("WeakerAccess")
    public ArzFile(String fileName) throws IOException {
        this(fileName, true);
    }

    @SuppressWarnings("WeakerAccess")
    public ArzFile(String fileName, boolean useCache) throws IOException {
        // ARZ header file format
        // 0x000000 int32
        // 0x000004 int32 start of dbRecord table
        // 0x000008 int32 size in bytes of dbRecord table
        // 0x00000c int32 numEntries in dbRecord table
        // 0x000010 int32 start of string table
        // 0x000014 int32 size in bytes of string table

        this.useCache = useCache;

        File file = new File(fileName);
        arzBuffer = ByteBuffer.allocate((Math.toIntExact(file.length()))).order(ByteOrder.LITTLE_ENDIAN);
        FileChannel in = new FileInputStream(file).getChannel();
        in.read(arzBuffer);
        arzBuffer.rewind();

        logger.log(System.Logger.Level.DEBUG, "Loaded arz file (''{0}''), ''{1}'' bytes.", file.getName(), arzBuffer.capacity());

        int[] header = new int[6];

        for (int i = 0; i < 6; i++) {
            header[i] = arzBuffer.getInt();
            logger.log(System.Logger.Level.TRACE, "Header[''{0}''] = ''{1}'' ''{2}''", i, header[i], String.format("%X", header[i]));
        }

        int recordsTableStart = header[1];
        int recordsTableCount = header[3];
        int stringsTableStart = header[4];

        readStringsTable(stringsTableStart);
        readRecordsTable(recordsTableStart, recordsTableCount);
        in.close();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public void invalidateCacheEntry(String recordId) {
        recordId = Util.normalizeRecordPath(recordId);
        CacheDbRecord.getInstance().remove(recordId);
    }

    @SuppressWarnings("WeakerAccess")
    public DbRecord getRecord(String recordId) {
        recordId = Util.normalizeRecordPath(recordId);
        if (exists(recordId)) {
            try {
                return recordDecode(recordId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean exists(String recordId) {
        recordId = Util.normalizeRecordPath(recordId);
        return (recordId != null
                && !recordId.isEmpty()
                && recordsMetadata.containsKey(recordId)
                && recordsMetadata.get(recordId) != null
                && recordsMetadata.get(recordId).getOffset() > 0);
    }

    // loads all strings and stores in strings array
    private void readStringsTable(int start) {
        arzBuffer.position(start);
        int numStrings = arzBuffer.getInt();
        stringsTable = new String[numStrings];

        logger.log(System.Logger.Level.DEBUG, "string table at ''{0}'' numstrings=''{1}''\n", String.format("%X", start), numStrings);

        for (int i = 0; i < numStrings; i++) {
            stringsTable[i] = readString();
            logger.log(System.Logger.Level.TRACE, "readStringsTable: ''{0}'' ''{1}''\n", i, stringsTable[i]);
        }
    }

    // loads all records metadata
    private void readRecordsTable(int start, int count) {
        recordsMetadata = new Hashtable<>();
        logger.log(System.Logger.Level.DEBUG, "recordsMetadata table at ''{0}'' numrecords=''{1}''\n", String.format("%X", start), count);
        arzBuffer.position(start);
        for (int i = 0; i < count; i++) {
            String recordId = recordMetadataGet();
            DbRecord r = recordsMetadata.get(recordId);
            logger.log(System.Logger.Level.TRACE, "readRecordsTable: ''{0}'' ''{1}'' ''{2}'' ''{3}''\n", r.getStringIndex(), stringsTable[r.getStringIndex()], r.getOffset(), recordId);
        }
    }

    private String readString() {
        String ret = null;
        int stringLen = arzBuffer.getInt();
        byte[] data = new byte[stringLen];
        arzBuffer.get(data, 0, stringLen);
        try {
            ret = new String(data, "CP1252");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // loads a single record metadata and store in records hashtable, returns the id
    private String recordMetadataGet() {
        // Record Entry Format
        // 0x0000 int32 stringEntryID (dbr filename)
        // 0x0004 int32 string length
        // 0x0008 string (record type)
        // 0x00?? int32 offset
        // 0x00?? int32 length in bytes
        // 0x00?? int32 timestamp?
        // 0x00?? int32 timestamp?

        int idStringIndex = arzBuffer.getInt();
        String recordType = readString();
        int offset = arzBuffer.getInt();

        //skips compressed size(32bit int) and 2 timestamps (32bit int each)
        arzBuffer.position(arzBuffer.position() + 12);

        logger.log(System.Logger.Level.TRACE, "recordMetadataGet: ''{0}'' ''{1}'' ''{2}''\n", idStringIndex, recordType, offset);

        DbRecord r = new DbRecord();
        r.setId(Util.normalizeRecordPath(stringsTable[idStringIndex]));
        r.setStringIndex(idStringIndex);
        r.setRecordType(recordType);
        r.setOffset(offset + 24);

        recordsMetadata.put(r.getId(), r);
        return r.getId();
    }

    // returns all variables and values from a single record
    private DbRecord recordDecode(String recordId) throws IOException {
        // 0x00 int16 specifies data type:
        //      0x0000 = int - data will be an int32
        //      0x0001 = float - data will be a Single
        //      0x0002 = string - data will be an int32 that is index into string table
        //      0x0003 = bool - data will be an int32
        // 0x02 int16 specifies number of values (usually 1, but sometimes more (for arrays)
        // 0x04 int32 key string ID (the id into the string table for this variable name
        // 0x08 data value

        String id = Util.normalizeRecordPath(recordId);
        if (id == null || !recordsMetadata.containsKey(id)) {
            throw new IOException(String.format("Record not found '%s'", id));
        }

        if (useCache && CacheDbRecord.getInstance().containsKey(id)) {
            return CacheDbRecord.getInstance().get(id);
        }

        byte[] data = recordDecompress(id);
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        if ((data.length % 4) != 0) {
            //data length not multiple of 4
            throw new RuntimeException("corrupt data found while decoding record");
        }
        DbRecord record = recordsMetadata.get(id);

        for (int i = 0; i < (data.length / 4); ) {
            short dataType = buffer.getShort();
            short valCount = buffer.getShort();
            int variableId = buffer.getInt();
            String variableName = stringsTable[variableId];
            logger.log(System.Logger.Level.TRACE, "recordDecode: ''{0}'' ''{1}'' ''{2}'' ''{3}''\n", variableName, dataType, valCount, variableId);

            if (dataType < 0 || dataType > 4 || variableName == null || variableName.isEmpty() || valCount < 0) {
                throw new RuntimeException("Error parsing record " + id);
            }

            DbVariable v = new DbVariable();
            v.setVariableName(variableName);
            v.setType(DbVariable.Type.valueOf(dataType));

            for (int j = 0; j < valCount; j++) {
                //integer=0 int32
                //float=1 float
                //string=2 string
                //boolean=3 int32
                //unknown=4 int32
                if (v.getType() == DbVariable.Type.Float) {
                    float val = buffer.getFloat();
                    v.addValue(val);
                } else if (v.getType() == DbVariable.Type.String) {
                    int stringId = buffer.getInt();
                    String val = stringsTable[stringId];
                    val = val.trim().replaceAll("[\r\n]", "");
                    v.addValue(val);
                } else if (v.getType() == DbVariable.Type.Boolean) {
                    boolean val = buffer.getInt() == 1;
                    v.addValue(val);
                } else {
                    //0 and 4
                    int val = buffer.getInt();
                    v.addValue(val);
                }

            }
            record.getVariables().put(v.getVariableName(), v);
            i += (2 + valCount);
        }

        if (useCache) {
            CacheDbRecord.getInstance().put(id, record);
        }
        return record;
    }

    //decompress a raw deflate(rfc1951) stream (a single record)
    private byte[] recordDecompress(String id) {
        DbRecord r = recordsMetadata.get(id);
        arzBuffer.position(r.getOffset() + 2);
        logger.log(System.Logger.Level.TRACE, "reading (''{0}'') from offset ''{1}'' type ''{2}'' (pos ''{3}'')\n", id, String.format("%X", r.getOffset()), r.getRecordType(), String.format("%X", arzBuffer.position()));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ByteArrayInputStream input = new ByteArrayInputStream(arzBuffer.array(), arzBuffer.position(), arzBuffer.limit());

        try {
            InflaterInputStream inflaterInputStream = new InflaterInputStream(input, new Inflater(true), 512);

            for (int b; (b = inflaterInputStream.read()) != -1; ) {
                buffer.write(b);
            }
            logger.log(System.Logger.Level.TRACE, "buffer size ", buffer.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toByteArray();
    }


}
