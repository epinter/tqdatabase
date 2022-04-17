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

package br.com.pinter.tqdatabase.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BOM {
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
