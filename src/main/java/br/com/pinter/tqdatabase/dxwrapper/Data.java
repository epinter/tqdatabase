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

package br.com.pinter.tqdatabase.dxwrapper;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"size", "content"})
public class Data extends Structure {
    private static final System.Logger logger = System.getLogger(Data.class.getName());

    public static class ByValue extends Data implements Structure.ByValue {
    }

    public int size;
    public Pointer content;

    public byte[] getContent() {
        System.err.println("SIZE IS " + size);
        if (content == null || size == 0) {
            logger.log(System.Logger.Level.ERROR, "INVALID CONTENT");
            Native.free(Pointer.nativeValue(content));
            return null;
        }
        byte[] d = content.getByteArray(0, size);
        Native.free(Pointer.nativeValue(content));

        return d;
    }
}
