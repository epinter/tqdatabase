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
import org.apache.commons.lang3.SystemUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class DxTexWrapper {
    private static native Data.ByValue convertDDStoPNG(long size, Buffer data);
    private static native Data.ByValue convertPNGtoDDS(long size, Buffer data, boolean dx10ext, boolean bgra);

    public enum Compat {
        DX9,
        DX10
    }

    public enum ColorSpace {
        BGRA,
        RGBA
    }

    static {
        String libraryArch = SystemUtils.OS_ARCH.equals("amd64") ? "x64" : "x86";

        System.load("dxtexwrapper-" + libraryArch + ".dll");
        Native.register("dxtexwrapper-" + libraryArch);
    }

    public static byte[] convertDDStoPNG(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Data.ByValue response = convertDDStoPNG(buffer.capacity(), buffer);

        return response.getContent();
    }

    public static byte[] convertPNGtoDDS(byte[] data, Compat compat, ColorSpace colorSpace) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Data.ByValue response;
        try {
            response = convertPNGtoDDS(buffer.capacity(), buffer,
                    compat.equals(Compat.DX10),
                    colorSpace.equals(ColorSpace.BGRA));
        } catch (Error e) {
            System.err.println("ERROR CONVERTING PNG TO DDS");
            e.printStackTrace();
            return null;
        }

        return response.getContent();
    }

}
