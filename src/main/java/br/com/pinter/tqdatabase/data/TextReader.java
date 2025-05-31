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

import br.com.pinter.tqdatabase.cache.CacheText;
import br.com.pinter.tqdatabase.models.ResourceType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class TextReader implements ArcEntryReader<Map<String, String>> {
    private static final System.Logger logger = System.getLogger(TextReader.class.getName());

    private Map<String, String> readTxt(String filename, ResourceReader resourceReader) throws IOException {
        Map<String, String> ret = new HashMap<>();
        byte[] d = resourceReader.getData(filename);
        try {
            BufferedReader br = new BufferedReader(new StringReader(new String(d, BOM.toCharset(Arrays.copyOf(d, 4)))));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("//")) {
                    continue;
                }
                line = line.replaceAll("//.*", "").trim();

                String[] kv = line.split("=");
                if (kv.length == 2 && kv[0] != null && kv[1] != null) {
                    if (resourceReader.isUseCache()) {
                        CacheText.getInstance().put(kv[0], kv[1]);
                    } else {
                        ret.put(kv[0], kv[1]);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(System.Logger.Level.ERROR, e);
            throw new IOException(e);
        }

        return ret;
    }

    @Override
    public Map<String, String> readAll(ResourceReader resourceReader) throws IOException {
        Map<String, String> ret = new HashMap<>();
        for (String tf : resourceReader.list(ResourceType.TEXT)) {
            ret.putAll(readTxt(tf, resourceReader));
        }
        return ret;
    }

    @Override
    public Map<String, String> readFile(ResourceReader resourceReader, String filename) throws IOException {
        return readTxt(filename, resourceReader);
    }

    static class BOM {
        private BOM() {
        }

        private static final byte[] BOM_UTF8 = new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
        private static final byte[] BOM_UTF16LE = new byte[]{(byte) 0xff, (byte) 0xfe};
        private static final byte[] BOM_UTF16BE = new byte[]{(byte) 0xfe, (byte) 0xff};
        private static final byte[] BOM_UTF32LE = new byte[]{(byte) 0xff, (byte) 0xfe, (byte) 0x00, (byte) 0x00};
        private static final byte[] BOM_UTF32BE = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xfe, (byte) 0xff};

        private static boolean bomCompare(byte[] bom, byte[] data) {
            boolean eq = true;
            for (int i = 0; eq && i < bom.length; i++) {
                eq = data[i] == bom[i];
            }
            return eq;
        }

        public static String toCharset(byte[] raw) {
            ByteBuffer data = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
            if (bomCompare(BOM_UTF8, data.array())) {
                return "UTF-8";
            } else if (bomCompare(BOM_UTF16LE, data.array())) {
                return "UTF-16LE";
            } else if (bomCompare(BOM_UTF16BE, data.array())) {
                return "UTF-16BE";
            } else if (bomCompare(BOM_UTF32LE, data.array())) {
                return "UTF-32LE";
            } else if (bomCompare(BOM_UTF32BE, data.array())) {
                return "UTF-32BE";
            } else {
                return "ISO8859-1";
            }
        }
    }
}
