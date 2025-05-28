/*
 * Copyright (C) 2025 Emerson Pinter - All Rights Reserved
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

import br.com.pinter.tqdatabase.TextureParseException;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.System.Logger.Level.DEBUG;

public class DDSHeader {
    private static final System.Logger logger = System.getLogger(DDSHeader.class.getName());

    // offset 0
    private final int dwSize;
    private int dwFlags;
    private final int dwHeight;
    private final int dwWidth;
    private int dwPitchOrLinearSize;
    private final int dwDepth;
    private int dwMipMapCount;
    private final byte[] dwReserved1 = new byte[4 * 11];

    // offset 72
    private final int pfDwSize;
    private int pfDwFlags;
    private final int pfDwFourCC;
    private final int pfDwRGBBitCount;
    private int pfDwRBitMask;
    private int pfDwGBitMask;
    private int pfDwBBitMask;
    private int pfDwABitMask;

    // offset 104
    private int dwCaps;
    private final int dwCaps2;
    private final int dwCaps3;
    private final int dwCaps4;
    private final int dwReserved2;
    private final ByteBuffer buffer;
    private final byte[] fourCc;
    private int surfaceSize;
    private boolean compressed;

    public DDSHeader(ByteBuffer buffer) {
        this.buffer = buffer;

        dwSize = buffer.getInt();
        dwFlags = buffer.getInt();
        dwHeight = buffer.getInt();
        dwWidth = buffer.getInt();
        dwPitchOrLinearSize = buffer.getInt();
        dwDepth = buffer.getInt();
        dwMipMapCount = buffer.getInt();
        buffer.get(dwReserved1);

        pfDwSize = buffer.getInt();
        pfDwFlags = buffer.getInt();
        pfDwFourCC = buffer.getInt();
        pfDwRGBBitCount = buffer.getInt();
        pfDwRBitMask = buffer.getInt();
        pfDwGBitMask = buffer.getInt();
        pfDwBBitMask = buffer.getInt();
        pfDwABitMask = buffer.getInt();
        dwCaps = buffer.getInt();
        dwCaps2 = buffer.getInt();
        dwCaps3 = buffer.getInt();
        dwCaps4 = buffer.getInt();
        dwReserved2 = buffer.getInt();

        if (dwSize != 124) {
            throw new TextureParseException("Invalid DDS_HEADER size: " + dwSize);
        }

        if (pfDwSize != 32) {
            throw new TextureParseException("Invalid DDS_PIXELFORMAT size: " + pfDwSize);
        }

        if ((dwCaps2 & Flags.DDSCAPS2_VOLUME) == Flags.DDSCAPS2_VOLUME
                && (dwCaps2 & Flags.DDSCAPS_COMPLEX) == Flags.DDSCAPS_COMPLEX) {
            throw new TextureParseException("VOLUME TEXTURE NOT IMPLEMENTED");
        }
        if ((dwCaps2 & Flags.DDSCAPS2_CUBEMAP) == Flags.DDSCAPS2_CUBEMAP
                && (dwCaps2 & Flags.DDSCAPS_COMPLEX) == Flags.DDSCAPS_COMPLEX) {
            throw new TextureParseException("CUBEMAP TEXTURE NOT IMPLEMENTED");
        }

        logger.log(DEBUG, "dwSize:{0}; dwFlags:{1};" +
                        " dwHeight:{2}; dwWidth:{3}; dwPitchOrLinearSize:{4};" +
                        " dwDepth:{5}; dwMipMapCount:{6};",
                dwSize, dwFlags, dwHeight, dwWidth, dwPitchOrLinearSize,
                dwDepth, dwMipMapCount);

        logger.log(DEBUG, "pfDwSize:{0}; pfDwFlags:0x{1};  pfDwFourCC:{2};  pfDwRGBBitCount:{3};" +
                        " pfDwRBitMask:{4}; pfDwGBitMask:{5}; pfDwBBitMask:{6}; pfDwABitMask:{7}; dwCaps:{8};",
                pfDwSize, Integer.toHexString(pfDwFlags), pfDwFourCC, pfDwRGBBitCount,
                pfDwRBitMask, pfDwGBitMask, pfDwBBitMask, pfDwABitMask, dwCaps);

        fourCc = new byte[]{
                (byte) (pfDwFourCC & 0xff),
                (byte) (pfDwFourCC >> 8 & 0xff),
                (byte) (pfDwFourCC >> 16 & 0xff),
                (byte) (pfDwFourCC >> 24 & 0xff),
        };
    }

    public void calculatePitch() {
        if (Arrays.equals(new byte[]{'D', 'X', 'T', '1'}, fourCc)
                || Arrays.equals(new byte[]{'B', 'C', '1'}, 0, 3, fourCc, 0, 3)
                || Arrays.equals(new byte[]{'B', 'C', '4'}, 0, 3, fourCc, 0, 3)) {
            dwPitchOrLinearSize = Math.max(1, ((dwWidth + 3) / 4)) * Math.max(1, ((dwHeight + 3) / 4)) * 8;
            surfaceSize = dwPitchOrLinearSize;
            compressed = true;
        } else if (Arrays.equals(new byte[]{'D', 'X', 'T'}, 0, 3, fourCc, 0, 3)
                || Arrays.equals(new byte[]{'B', 'C'}, 0, 2, fourCc, 0, 2)) {
            dwPitchOrLinearSize = Math.max(1, ((dwWidth + 3) / 4)) * Math.max(1, ((dwHeight + 3) / 4)) * 16;
            surfaceSize = dwPitchOrLinearSize;
            compressed = true;
        } else if (Arrays.equals(new byte[]{'R', 'G', 'B', 'G'}, 0, 4, fourCc, 0, 4)
                || Arrays.equals(new byte[]{'G', 'R', 'G', 'B'}, 0, 4, fourCc, 0, 4)
                || Arrays.equals(new byte[]{'U', 'Y', 'V', 'Y'}, 0, 4, fourCc, 0, 4)
                || Arrays.equals(new byte[]{'Y', 'U', 'Y', '2'}, 0, 4, fourCc, 0, 4)) {
            dwPitchOrLinearSize = ((dwWidth + 1) >> 1) * 4;
            surfaceSize = dwPitchOrLinearSize * dwHeight;
            compressed = false;
        } else if (((pfDwFlags & Flags.DDPF_RGB) == Flags.DDPF_RGB
                || (pfDwFlags & Flags.DDPF_LUMINANCE) == Flags.DDPF_LUMINANCE
                || (pfDwFlags & Flags.DDPF_YUV) == Flags.DDPF_YUV
                || (pfDwFlags & Flags.DDS_BUMPDUDV) == Flags.DDS_BUMPDUDV)
                && pfDwRGBBitCount > 0) {
            dwPitchOrLinearSize = ((dwWidth * pfDwRGBBitCount) + 7) / 8;
            surfaceSize = dwPitchOrLinearSize * dwHeight;
            compressed = false;
        } else {
            String msg = "NOT IMPLEMENTED: fourCc=0x%s; pfDwFlags=0x%s;";
            if ((pfDwFlags & Flags.DDS_BUMPLUMINANCE) == Flags.DDS_BUMPLUMINANCE) {
                msg = "Legacy flag DDS_BUMPLUMINANCE and the formats that use it are not supported;" +
                        " fourCc=%s; pfDwFlags=%s;";
            }
            throw new TextureParseException(String.format(msg, Integer.toHexString(pfDwFourCC), Integer.toHexString(pfDwFlags)));
        }
    }

    public void fixHeader() {
        if (compressed) {
            dwFlags |= Flags.DDSD_LINEARSIZE;
        } else {
            dwFlags |= Flags.DDSD_PITCH;
        }

        dwMipMapCount = dwMipMapCount == 0 ? 1 : dwMipMapCount;
        buffer.putInt(24, dwMipMapCount);
        dwFlags |= Flags.DDSD_MIPMAPCOUNT;

        //save calculated  dwPitchOrLinearSize
        buffer.putInt(16, dwPitchOrLinearSize);

        //save flags
        buffer.putInt(4, dwFlags);

        //DDSD_PIXELFORMAT - Required in every .dds file.
        dwCaps |= Flags.DDSD_PIXELFORMAT;
        buffer.putInt(104, dwCaps);
    }

    /**
     * Fix RGB bitmasks
     */
    public void setRgbBitMasks() {
        if ((pfDwFlags & Flags.DDPF_RGB) == Flags.DDPF_RGB) {
            if (pfDwRGBBitCount >= 24 && (pfDwRBitMask == 0 || pfDwGBitMask == 0 || pfDwBBitMask == 0)) {
                // set masks for A8R8G8B8
                // For A8R8G8B8, the green mask is 0x000000ff.
                pfDwBBitMask = 0x000000ff;
                buffer.putInt(96, pfDwBBitMask);
                // For A8R8G8B8, the green mask is 0x0000ff00.
                pfDwGBitMask = 0x0000ff00;
                buffer.putInt(92, pfDwGBitMask);
                // For A8R8G8B8, the red mask is 0x00ff0000.
                pfDwRBitMask = 0x00ff0000;
                buffer.putInt(88, pfDwRBitMask);
            }
            if (pfDwRGBBitCount == 32 && (pfDwABitMask == 0 || (pfDwFlags & Flags.DDPF_ALPHAPIXELS) == 0)) {
                // enable DDS_ALPHAPIXELS
                pfDwFlags |= Flags.DDPF_ALPHAPIXELS;
                buffer.putInt(76, pfDwFlags);
                // For A8R8G8B8, the alpha mask is 0xff000000.
                pfDwABitMask = 0xff000000;
                buffer.putInt(100, pfDwABitMask);
            }
        }
        if ((pfDwFlags & Flags.DDS_BUMPDUDV) == Flags.DDS_BUMPDUDV) {
            if (pfDwRGBBitCount == 16 && (pfDwRBitMask == 0 || pfDwGBitMask == 0)) {
                //V8U8
                pfDwRBitMask = 0x00ff;
                buffer.putInt(88, pfDwRBitMask);
                pfDwGBitMask = 0xff00;
                buffer.putInt(92, pfDwGBitMask);
            }
            if (pfDwRGBBitCount == 32 && (pfDwRBitMask == 0 || pfDwGBitMask == 0)) {
                //V16U16
                pfDwRBitMask = 0x0000ffff;
                buffer.putInt(88, pfDwRBitMask);
                pfDwGBitMask = 0xffff0000;
                buffer.putInt(92, pfDwGBitMask);
            }
        }
    }

    public void zeroRgbBitMasks() {
        if ((pfDwFlags & Flags.DDPF_RGB) == Flags.DDPF_RGB) {
            if (pfDwRGBBitCount >= 24 && (pfDwRBitMask != 0 || pfDwGBitMask != 0 || pfDwBBitMask != 0)) {
                //set masks to zero
                pfDwBBitMask = 0;
                buffer.putInt(96, pfDwBBitMask);
                pfDwGBitMask = 0;
                buffer.putInt(92, pfDwGBitMask);
                pfDwRBitMask = 0;
                buffer.putInt(88, pfDwRBitMask);
            }
            if (pfDwRGBBitCount == 32 && (pfDwABitMask != 0 || (pfDwFlags & Flags.DDPF_ALPHAPIXELS) != 0)) {
                //disable DDS_ALPHAPIXELS
                pfDwFlags &= ~Flags.DDPF_ALPHAPIXELS;
                buffer.putInt(76, pfDwFlags);
                //set alpha channel bitmask
                pfDwABitMask = 0;
                buffer.putInt(100, pfDwABitMask);
            }
        }
        if ((pfDwFlags & Flags.DDS_BUMPDUDV) == Flags.DDS_BUMPDUDV) {
            if (pfDwRGBBitCount == 16 && (pfDwRBitMask == 0 || pfDwGBitMask == 0)) {
                //V8U8
                pfDwRBitMask = 0;
                buffer.putInt(88, pfDwRBitMask);
                pfDwGBitMask = 0;
                buffer.putInt(92, pfDwGBitMask);
            }
            if (pfDwRGBBitCount == 32 && (pfDwRBitMask == 0 || pfDwGBitMask == 0)) {
                //V16U16
                pfDwRBitMask = 0;
                buffer.putInt(88, pfDwRBitMask);
                pfDwGBitMask = 0;
                buffer.putInt(92, pfDwGBitMask);
            }
        }
    }

    public void setDwMipMapCount(int dwMipMapCount) {
        this.dwMipMapCount = dwMipMapCount;
        buffer.putInt(24, this.dwMipMapCount);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getDwSize() {
        return dwSize;
    }

    public int getDwFlags() {
        return dwFlags;
    }

    public int getDwHeight() {
        return dwHeight;
    }

    public int getDwWidth() {
        return dwWidth;
    }

    public int getDwPitchOrLinearSize() {
        return dwPitchOrLinearSize;
    }

    public int getDwDepth() {
        return dwDepth;
    }

    public int getDwMipMapCount() {
        return dwMipMapCount;
    }

    public byte[] getDwReserved1() {
        return dwReserved1;
    }

    public int getPfDwSize() {
        return pfDwSize;
    }

    public int getPfDwFlags() {
        return pfDwFlags;
    }

    public int getPfDwFourCC() {
        return pfDwFourCC;
    }

    public int getPfDwRGBBitCount() {
        return pfDwRGBBitCount;
    }

    public int getPfDwRBitMask() {
        return pfDwRBitMask;
    }

    public int getPfDwGBitMask() {
        return pfDwGBitMask;
    }

    public int getPfDwBBitMask() {
        return pfDwBBitMask;
    }

    public int getPfDwABitMask() {
        return pfDwABitMask;
    }

    public int getDwCaps() {
        return dwCaps;
    }

    public int getDwCaps2() {
        return dwCaps2;
    }

    public int getDwCaps3() {
        return dwCaps3;
    }

    public int getDwCaps4() {
        return dwCaps4;
    }

    public byte[] getFourCc() {
        return fourCc;
    }

    public int getSurfaceSize() {
        return surfaceSize;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public int getDwReserved2() {
        return dwReserved2;
    }

    public static final class Flags {
        //header dwFlags
        public static final int DDSD_CAPS = 0x1;
        public static final int DDSD_HEIGHT = 0x2;
        public static final int DDSD_WIDTH = 0x4;
        public static final int DDSD_PITCH = 0x8;
        public static final int DDSD_PIXELFORMAT = 0x1000;
        public static final int DDSD_MIPMAPCOUNT = 0x20000;
        public static final int DDSD_LINEARSIZE = 0x80000;
        public static final int DDSD_DEPTH = 0x800000;

        //header dwCaps
        public static final int DDSCAPS_COMPLEX = 0x8;
        public static final int DDSCAPS_TEXTURE = 0x1000;
        public static final int DDSCAPS_MIPMAP = 0x400000;

        //header dwCaps2
        public static final int DDSCAPS2_CUBEMAP = 0x200;
        public static final int DDSCAPS2_CUBEMAP_POSITIVEX = 0x400;
        public static final int DDSCAPS2_CUBEMAP_NEGATIVEX = 0x800;
        public static final int DDSCAPS2_CUBEMAP_POSITIVEY = 0x1000;
        public static final int DDSCAPS2_CUBEMAP_NEGATIVEY = 0x2000;
        public static final int DDSCAPS2_CUBEMAP_POSITIVEZ = 0x4000;
        public static final int DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x8000;
        public static final int DDSCAPS2_VOLUME = 0x200000;

        //pixelformat dwFlags
        public static final int DDPF_ALPHAPIXELS = 0x1;
        public static final int DDPF_ALPHA = 0x2;
        public static final int DDPF_FOURCC = 0x4;
        public static final int DDPF_RGB = 0x40;
        public static final int DDPF_YUV = 0x200;
        public static final int DDPF_LUMINANCE = 0x20000;
        public static final int DDS_BUMPDUDV = 0x80000;
        public static final int DDS_BUMPLUMINANCE = 0x00040000;
    }

    public int calculateMipMapSize(int mipWidth, int mipHeight) {
        if (Arrays.equals(new byte[]{'D', 'X', 'T', '1'}, fourCc)
                || Arrays.equals(new byte[]{'B', 'C', '1'}, 0, 3, fourCc, 0, 3)
                || Arrays.equals(new byte[]{'B', 'C', '4'}, 0, 3, fourCc, 0, 3)
        ) {
            return Math.max(1, ((mipWidth + 3) / 4)) * Math.max(1, ((mipHeight + 3) / 4)) * 8;
        } else if (Arrays.equals(new byte[]{'D', 'X', 'T'}, 0, 3, fourCc, 0, 3)
                || Arrays.equals(new byte[]{'B', 'C'}, 0, 2, fourCc, 0, 2)) {
            return Math.max(1, ((mipWidth + 3) / 4)) * Math.max(1, ((mipHeight + 3) / 4)) * 16;
        } else if (pfDwRGBBitCount > 0) {
            return (mipWidth * mipHeight * pfDwRGBBitCount) / 8;
        } else {
            throw new TextureParseException(String.format("UNABLE TO CALCULATE MIPMAP SIZE: fourCc=%s", Integer.toHexString(pfDwFourCC)));
        }
    }

}
