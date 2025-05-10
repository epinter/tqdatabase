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

package br.com.pinter.tqdatabase.models;

import br.com.pinter.tqdatabase.dxwrapper.DxTexWrapper;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class Texture implements Comparable<Texture> {
    private static final System.Logger logger = System.getLogger(Texture.class.getName());

    private static final byte[] MAGIC_TEX = new byte[]{0x54, 0x45, 0x58};
    private static final byte[] MAGIC_PNG = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] MAGIC_DDS = new byte[]{0x44, 0x44, 0x53, 0x20};
    private static final byte[] MAGIC_DDSR = new byte[]{0x44, 0x44, 0x53, 0x52};

    private static final byte[] MAGIC_BMP = new byte[]{0x42, 0x4D};

    private static final int DDSD_PIXELFORMAT = 0x00001000;
    private static final int DDPF_ALPHAPIXELS = 0x00000001;
    private static final int DDPF_RGB = 0x40;
    private static final int DDSD_HEIGHT = 0x2;
    private static final int DDSD_WIDTH = 0x4;

    private static final String ERROR_MESSAGE = "conversion from '%s' to '%s' is not implemented";
    private final Path filename;
    private final byte[] data;
    private TextureType type = TextureType.UNKNOWN;
    private int textureSize;
    private int width;
    private int height;
    private final byte[] md5sum;

    public Texture(Path filename, byte[] data) {
        this.filename = filename;
        this.data = data;

        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Texture data can't be empty");
        }

        try {
            this.md5sum = MessageDigest.getInstance("MD5").digest(this.data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        byte[] containerMagic = Arrays.copyOf(data, 256);

        if (Arrays.compare(containerMagic, 0, MAGIC_TEX.length, MAGIC_TEX, 0, MAGIC_TEX.length) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            int textureVersion = buffer.getInt(3);
            int ddsOffset;
            if (textureVersion == 1) {
                //version 1
                textureSize = buffer.getInt(8);
                type = TextureType.TEXV1;
                ddsOffset = 12;
            } else if (textureVersion == 2) {
                //version 2
                textureSize = buffer.getInt(9);
                type = TextureType.TEXV2;
                ddsOffset = 13;
            } else {
                throw new IllegalStateException("invalid tex version");
            }
            height = buffer.getInt(ddsOffset + 12);
            width = buffer.getInt(ddsOffset + 16);

            if (textureSize <= 0 || textureSize > data.length) {
                throw new IllegalStateException("invalid texture size");
            }
            buffer.clear();
        } else if (Arrays.compare(containerMagic, 0, MAGIC_DDS.length, MAGIC_DDS, 0, MAGIC_DDS.length) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(4);
            int ddsHeaderStructSize = buffer.getInt();
            int ddsHeaderFlags = buffer.getInt();
            int pixelFormatSize = buffer.getInt(76);

            if (ddsHeaderStructSize == 124 && pixelFormatSize == 32) {
                type = TextureType.DDS;
                textureSize = data.length;
                if ((ddsHeaderFlags & DDSD_HEIGHT) == DDSD_HEIGHT) {
                    height = buffer.getInt(12);
                }
                if ((ddsHeaderFlags & DDSD_WIDTH) == DDSD_WIDTH) {
                    width = buffer.getInt(16);
                }
            }
            if (type != TextureType.DDS) {
                throw new IllegalStateException("invalid dds header ");
            }
        } else if (Arrays.compare(containerMagic, 0, MAGIC_PNG.length, MAGIC_PNG, 0, MAGIC_PNG.length) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

            buffer.position(MAGIC_PNG.length);
            int ihdrSize = buffer.getInt();

            byte[] chunkType = new byte[4];
            buffer.get(chunkType);
            byte[] ihdr = new byte[ihdrSize];
            int ihdrOffset = buffer.position();
            buffer.get(ihdr);
            if (Arrays.compare(chunkType, 0, 4, new byte[]{0x49, 0x48, 0x44, 0x52}, 0, 4) == 0) {
                width = buffer.getInt(ihdrOffset);
                height = buffer.getInt(ihdrOffset + 4);
                int depth = ihdr[8];

                logger.log(System.Logger.Level.INFO, "PNG bitdepth:{0}; chunkType:{1}; ihdrSize:{2}; width:{3}; height:{4};",
                        depth, new String(chunkType), ihdrSize, width, height);
                if (depth == 1 || depth == 2 || depth == 4 || depth == 8 || depth == 16) {
                    textureSize = data.length;
                    type = TextureType.PNG;
                }
            }
            if (type != TextureType.PNG) {
                throw new IllegalStateException("invalid png header");
            }
            buffer.clear();
        } else if (Arrays.compare(containerMagic, 0, MAGIC_BMP.length, MAGIC_BMP, 0, MAGIC_BMP.length) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(2); //skip magic
            int bmpSize = buffer.getInt();

            if (bmpSize == data.length) {
                textureSize = bmpSize;
                type = TextureType.BMP;
            } else {
                throw new IllegalStateException("invalid texture size");
            }

            buffer.getInt(); //skip reserved bytes
            buffer.getInt(); //skip pixel array offset
            int bmpHeaderSize = buffer.getInt();
            switch (bmpHeaderSize) {
                case 12 -> {
                    width = buffer.getShort();
                    height = buffer.getShort();
                }
                case 40, 52, 56, 108, 124 -> {
                    width = buffer.getInt();
                    height = buffer.getInt();
                }
            }
            buffer.clear();
        }
    }

    public Texture(Path filename) throws IOException {
        this(filename, Files.readAllBytes(filename));
    }

    public Texture(String filename, byte[] data) {
        this(Path.of(filename), data);
    }

    public Texture(String filename) throws IOException {
        this(Path.of(filename));
    }

    public Path getFilename() {
        return filename;
    }

    public TextureType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture convert(TextureType to) {
        if (type == to) {
            throw new IllegalArgumentException("Invalid texture type, source and destination types are equal");
        }

        if (to.equals(TextureType.DDS)) {
            switch (type) {
                case TEXV1, TEXV2 -> {
                    return new Texture(filename, convertTexHeaderToDds());
                }
                case PNG -> {
                    return new Texture(filename,
                            DxTexWrapper.convertPNGtoDDS(data, DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA));
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TextureType.TEXV1)) {
            switch (type) {
                case DDS -> {
                    return new Texture(filename, convertDdsHeaderToTex(TextureType.TEXV1));
                }
                case PNG -> {
                    //TEXv1 doesn't have DX10 extension header
                    return new Texture(filename, DxTexWrapper.convertPNGtoDDS(data,
                            DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA)).convert(TextureType.TEXV1);
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TextureType.TEXV2)) {
            switch (type) {
                case DDS -> {
                    return new Texture(filename, convertDdsHeaderToTex(TextureType.TEXV2));
                }
                case PNG -> {
                    //TEXv2 doesn't have DX10 extension header
                    return new Texture(filename, DxTexWrapper.convertPNGtoDDS(data,
                            DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA)).convert(TextureType.TEXV2);
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TextureType.PNG)) {
            switch (type) {
                case TEXV1, TEXV2 -> {
                    return new Texture(filename, convertTexHeaderToDds()).convert(TextureType.PNG);
                }
                case DDS -> {
                    return new Texture(filename, DxTexWrapper.convertDDStoPNG(data));
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        }
        throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
    }

    private byte[] convertDdsHeaderToTex(TextureType version) {
        if ((type.equals(TextureType.TEXV1) || type.equals(TextureType.TEXV2))) {
            throw new IllegalArgumentException("texture doesn't need conversion");
        }


        int ddsOffset;
        byte byteVersion;
        int ddsTextureSize = data.length;
        if (version.equals(TextureType.TEXV1)) {
            ddsOffset = 12;
            byteVersion = 0x01;
        } else if (version.equals(TextureType.TEXV2)) {
            ddsOffset = 13;
            byteVersion = 0x02;
        } else {
            throw new UnsupportedOperationException("texture type not suppored");
        }
        ByteBuffer preamble = ByteBuffer.allocate(ddsOffset).order(ByteOrder.LITTLE_ENDIAN);
        preamble.put(MAGIC_TEX);
        preamble.put(byteVersion);
        preamble.putInt(0);
        int texFlagOffset = preamble.position();
        if (version.equals(TextureType.TEXV2)) {
            //alpha channel ?
            preamble.put((byte) 1);
        }
        preamble.putInt(ddsTextureSize);
        preamble.position(0);

        ByteBuffer tex = ByteBuffer.allocate(preamble.limit() + data.length).order(ByteOrder.LITTLE_ENDIAN)
                .put(preamble).put(data).position(preamble.limit());
        tex.put(MAGIC_DDSR);

        logger.log(System.Logger.Level.INFO, "filename:{0}; size:{1}; texVersion:{2}; ddsOffset:{3};",
                filename, tex.capacity(), type, ddsOffset);

        int ddsHeaderStructSize = tex.getInt();
        int ddsHeaderFlags = tex.getInt();
        int ddsHeaderHeight = tex.getInt();
        int ddsHeaderWidth = tex.getInt();
        int ddsHeaderPitchOrLinearSize = tex.getInt();
        int ddsHeaderDepth = tex.getInt();
        int ddsHeaderMipMapCount = tex.getInt();
        tex.position(tex.position() + (4 * 11));
        logger.log(System.Logger.Level.INFO, "filename:''{0}''; ddsHeaderStructSize:{1}; ddsHeaderFlags:{2};" +
                        " ddsHeaderHeight:{3}; ddsHeaderWidth:{4}; ddsHeaderPitchOrLinearSize:{5};" +
                        " ddsHeaderDepth:{6}; ddsHeaderMipMapCount:{7};",
                filename, ddsHeaderStructSize, ddsHeaderFlags, ddsHeaderHeight, ddsHeaderWidth, ddsHeaderPitchOrLinearSize,
                ddsHeaderDepth, ddsHeaderMipMapCount);

        int pixelFormatSize = tex.getInt();
        int pixelFormatFlagsOffset = tex.position();
        int pixelFormatFlags = tex.getInt();
        int pixelFormatFourCC = tex.getInt();
        int pixelFormatRGBBitCount = tex.getInt();
        int pixelFormatRBitMaskOffset = tex.position();
        int pixelFormatRBitMask = tex.getInt();
        int pixelFormatGBitMaskOffset = tex.position();
        int pixelFormatGBitMask = tex.getInt();
        int pixelFormatBBitMaskOffset = tex.position();
        int pixelFormatBBitMask = tex.getInt();
        int pixelFormatABitMaskOffset = tex.position();
        int pixelFormatABitMask = tex.getInt();
        int ddsHeaderCapsOffset = tex.position();
        int ddsHeaderCaps = tex.getInt();

        logger.log(System.Logger.Level.INFO, "filename:''{0}''; pixelFormatSize:{1}; pixelFormatFlagsOffset:{2};" +
                        " pixelFormatFlags:{3};  pixelFormatFourCC:{4};  pixelFormatRGBBitCount:{5}; pixelFormatRBitMaskOffset:{6};" +
                        " pixelFormatRBitMask:{7}; pixelFormatGBitMaskOffset:{8}; pixelFormatGBitMask:{9}; pixelFormatBBitMaskOffset:{10};" +
                        " pixelFormatBBitMask:{11}; pixelFormatABitMaskOffset:{12}; pixelFormatABitMask:{13}; ddsHeaderCapsOffset:{14};" +
                        " ddsHeaderCaps:{15};", filename,
                pixelFormatSize, pixelFormatFlagsOffset, pixelFormatFlags, pixelFormatFourCC, pixelFormatRGBBitCount,
                pixelFormatRBitMaskOffset, pixelFormatRBitMask, pixelFormatGBitMaskOffset, pixelFormatGBitMask,
                pixelFormatBBitMaskOffset, pixelFormatBBitMask, pixelFormatABitMaskOffset, pixelFormatABitMask,
                ddsHeaderCapsOffset, ddsHeaderCaps);

        if ((pixelFormatFlags & DDPF_RGB) == DDPF_RGB) {
            if (pixelFormatRGBBitCount >= 24 && (pixelFormatRBitMask != 0 || pixelFormatGBitMask != 0 || pixelFormatBBitMask != 0)) {
                //set masks to zero
                tex.put(pixelFormatBBitMaskOffset, new byte[]{0, 0, 0, 0});
                tex.put(pixelFormatGBitMaskOffset, new byte[]{0, 0, 0, 0});
                tex.put(pixelFormatRBitMaskOffset, new byte[]{0, 0, 0, 0});
            }
            if (pixelFormatRGBBitCount == 32 && (pixelFormatABitMask != 0 || (pixelFormatFlags & 1) != 0)) {
                //enable DDS_ALPHAPIXELS
                tex.put(pixelFormatFlagsOffset, (byte) (tex.get(pixelFormatFlagsOffset) & (~DDPF_ALPHAPIXELS)));
                //set alpha channel bitmask
                tex.put(pixelFormatABitMaskOffset, new byte[]{0, 0, 0, 0});
            }
        }
        //unknown byte on texv2, many textures without alpha channel have it set to 0, textures with alpha have it set to 1
        tex.put(texFlagOffset, (byte) (pixelFormatRGBBitCount == 32 ? 1 : 0));

        tex.putInt(ddsHeaderCapsOffset, ((tex.getInt(ddsHeaderCapsOffset) & (~DDSD_PIXELFORMAT))));

        return tex.array();
    }

    private byte[] convertTexHeaderToDds() {
        if (!(type.equals(TextureType.TEXV1) || type.equals(TextureType.TEXV2))) {
            throw new IllegalArgumentException("texture doesn't need conversion");
        }

        ByteBuffer tex = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        tex.position(0);

        int ddsOffset;
        if (type.equals(TextureType.TEXV1)) {
            ddsOffset = 12;
        } else if (type.equals(TextureType.TEXV2)) {
            ddsOffset = 13;
        } else {
            throw new UnsupportedOperationException("texture type not suppored");
        }

        if (textureSize <= 0 || textureSize > tex.capacity()) {
            throw new IllegalStateException("invalid texture size");
        }

        logger.log(System.Logger.Level.INFO, "filename:{0}; size:{1}; texVersion:{2}; ddsOffset:{3};",
                filename, tex.capacity(), type, ddsOffset);

        // remove 'R' from DDSR magic
        tex.put(ddsOffset + 3, (byte) 0x20);
        tex.position(ddsOffset + 4);

        /*
        typedef struct {
          DWORD           dwSize;
          DWORD           dwFlags;
          DWORD           dwHeight;
          DWORD           dwWidth;
          DWORD           dwPitchOrLinearSize;
          DWORD           dwDepth;
          DWORD           dwMipMapCount;
          DWORD           dwReserved1[11];
          DDS_PIXELFORMAT ddspf;
          DWORD           dwCaps;
          DWORD           dwCaps2;
          DWORD           dwCaps3;
          DWORD           dwCaps4;
          DWORD           dwReserved2;
        } DDS_HEADER;
        */

        int ddsHeaderStructSize = tex.getInt();
        int ddsHeaderFlags = tex.getInt();
        int ddsHeaderHeight = tex.getInt();
        int ddsHeaderWidth = tex.getInt();
        int ddsHeaderPitchOrLinearSize = tex.getInt();
        int ddsHeaderDepth = tex.getInt();
        int ddsHeaderMipMapCount = tex.getInt();
        tex.position(tex.position() + (4 * 11));
        logger.log(System.Logger.Level.INFO, "filename:''{0}''; ddsHeaderStructSize:{1}; ddsHeaderFlags:{2};" +
                        " ddsHeaderHeight:{3}; ddsHeaderWidth:{4}; ddsHeaderPitchOrLinearSize:{5};" +
                        " ddsHeaderDepth:{6}; ddsHeaderMipMapCount:{7};",
                filename, ddsHeaderStructSize, ddsHeaderFlags, ddsHeaderHeight, ddsHeaderWidth, ddsHeaderPitchOrLinearSize,
                ddsHeaderDepth, ddsHeaderMipMapCount);

        /*
        struct DDS_PIXELFORMAT {
            DWORD dwSize;
            DWORD dwFlags;
            DWORD dwFourCC;
            DWORD dwRGBBitCount;
            DWORD dwRBitMask;
            DWORD dwGBitMask;
            DWORD dwBBitMask;
            DWORD dwABitMask;
        };
        */

        int pixelFormatSize = tex.getInt();
        int pixelFormatFlagsOffset = tex.position();
        int pixelFormatFlags = tex.getInt();
        int pixelFormatFourCC = tex.getInt();
        int pixelFormatRGBBitCount = tex.getInt();
        int pixelFormatRBitMaskOffset = tex.position();
        int pixelFormatRBitMask = tex.getInt();
        int pixelFormatGBitMaskOffset = tex.position();
        int pixelFormatGBitMask = tex.getInt();
        int pixelFormatBBitMaskOffset = tex.position();
        int pixelFormatBBitMask = tex.getInt();
        int pixelFormatABitMaskOffset = tex.position();
        int pixelFormatABitMask = tex.getInt();
        int ddsHeaderCapsOffset = tex.position();
        int ddsHeaderCaps = tex.getInt();

        logger.log(System.Logger.Level.INFO, "filename:''{0}''; pixelFormatSize:{1}; pixelFormatFlagsOffset:{2};" +
                        " pixelFormatFlags:{3};  pixelFormatFourCC:{4};  pixelFormatRGBBitCount:{5}; pixelFormatRBitMaskOffset:{6};" +
                        " pixelFormatRBitMask:{7}; pixelFormatGBitMaskOffset:{8}; pixelFormatGBitMask:{9}; pixelFormatBBitMaskOffset:{10};" +
                        " pixelFormatBBitMask:{11}; pixelFormatABitMaskOffset:{12}; pixelFormatABitMask:{13}; ddsHeaderCapsOffset:{14};" +
                        " ddsHeaderCaps:{15};", filename,
                pixelFormatSize, pixelFormatFlagsOffset, pixelFormatFlags, pixelFormatFourCC, pixelFormatRGBBitCount,
                pixelFormatRBitMaskOffset, pixelFormatRBitMask, pixelFormatGBitMaskOffset, pixelFormatGBitMask,
                pixelFormatBBitMaskOffset, pixelFormatBBitMask, pixelFormatABitMaskOffset, pixelFormatABitMask,
                ddsHeaderCapsOffset, ddsHeaderCaps);

        if ((pixelFormatFlags & DDPF_RGB) == DDPF_RGB) {
            if (pixelFormatRGBBitCount >= 24 && (pixelFormatRBitMask == 0 || pixelFormatGBitMask == 0 || pixelFormatBBitMask == 0)) {
                //set masks for BGRA
                tex.put(pixelFormatBBitMaskOffset, new byte[]{(byte) 0xff, 0x00, 0x00, 0x00});
                tex.put(pixelFormatGBitMaskOffset, new byte[]{0x00, (byte) 0xff, 0x00, 0x00});
                tex.put(pixelFormatRBitMaskOffset, new byte[]{0x00, 0x00, (byte) 0xff, 0x00});
            }
            if (pixelFormatRGBBitCount == 32 && (pixelFormatABitMask == 0 || (pixelFormatFlags & 1) == 0)) {
                //enable DDS_ALPHAPIXELS
                tex.putInt(pixelFormatFlagsOffset, (tex.get(pixelFormatFlagsOffset) | DDPF_ALPHAPIXELS));
                //set alpha channel bitmask
                tex.put(pixelFormatABitMaskOffset, new byte[]{0x00, 0x00, 0x00, (byte) 0xff});
            }
        }
        tex.putInt(ddsHeaderCapsOffset, (byte) (tex.getInt(ddsHeaderCapsOffset) | DDSD_PIXELFORMAT));

        tex.position(ddsOffset);
        byte[] ret = new byte[tex.capacity() - tex.position()];
        tex.get(ret);
        tex.clear();
        return ret;
    }

    @Override
    public int compareTo(Texture o) {
        return o != null ? filename.compareTo(o.filename) : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return textureSize == texture.textureSize && width == texture.width && height == texture.height &&
                filename.equals(texture.filename) && type == texture.type && Arrays.equals(md5sum, texture.md5sum);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(filename, textureSize);
        result = 31 * result + Arrays.hashCode(md5sum);
        return result;
    }

    @Override
    public String toString() {
        return "Texture{" +
                "filename=" + filename +
                ", type=" + type +
                ", textureSize=" + textureSize +
                ", width=" + width +
                ", height=" + height +
                ", md5sum=" + Arrays.toString(md5sum) +
                '}';
    }
}
