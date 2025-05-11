/*
 * Copyright (C) 2025 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.data;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class RandomAccessFileLE extends RandomAccessFile {
    public RandomAccessFileLE(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    /**
     * Reads an int (4 bytes) using little-endian order.
     *
     * @return the int
     * @throws java.io.IOException if data can't be read
     */
    public int readIntLE() throws IOException {
        byte[] data = new byte[4];
        readFully(data, 0, data.length);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public short readInt8LE() throws IOException {
        byte[] data = new byte[2];
        readFully(data, 0, 1);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).put(data).rewind().getShort();
    }

    public short readInt8LE(int offset) throws IOException {
        long pos = getFilePointer();
        seek(offset);
        short res = readInt8LE();
        seek(pos);
        return res;
    }

    public byte[] get(int offset, int length) throws IOException {
        long pos = getFilePointer();
        seek(offset);
        byte[] data = new byte[length];
        readFully(data, 0, data.length);
        seek(pos);
        return data;
    }

    public short readShortLE() throws IOException {
        byte[] data = new byte[2];
        readFully(data, 0, data.length);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).put(data).rewind().getShort();
    }

    public short readShortLE(int offset) throws IOException {
        long pos = getFilePointer();
        seek(offset);
        short res = readShortLE();
        seek(pos);
        return res;
    }

    public void writeIntLE(int val) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(0, val);
        byte[] data = new byte[4];
        byteBuffer.get(data);
        write(data);
    }

    /**
     * Seeks to offset and reads dst.length of bytes. File pointer is returned to previous position after read.
     *
     * @param offset file position to read the bytes from
     * @param dst    buffer to write the data
     * @throws IOException if data can't be read
     */
    public void get(int offset, byte[] dst) throws IOException {
        long pos = getFilePointer();
        seek(offset);
        readFully(dst, 0, dst.length);
        seek(pos);
    }

    public String readPrefixedString() throws IOException {
        int strSize = readIntLE();
        byte[] buf = new byte[strSize];
        readFully(buf, 0, buf.length);
        return new String(buf, StandardCharsets.UTF_8);
    }

    public void writePrefixedString(String str) throws IOException {
        byte[] data = encodeString(str);
        ByteBuffer buf = ByteBuffer.allocate(data.length + 4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(data.length);
        buf.put(data);
        write(buf.array());
    }

    private static byte[] encodeString(String str) {
        ByteBuffer buffer = ByteBuffer.allocate(str.length());

        for (char o : str.toCharArray()) {
            char c = StringUtils.stripAccents(Character.toString(o)).toCharArray()[0];
            buffer.put(new byte[]{(byte) c});
        }
        return buffer.array();
    }

}
