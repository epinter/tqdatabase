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

import br.com.pinter.tqdatabase.TextureParseException;
import br.com.pinter.tqdatabase.data.DDSHeader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.System.Logger.Level.DEBUG;

public class Texture extends ResourceFile implements Comparable<Texture> {
    private static final System.Logger logger = System.getLogger(Texture.class.getName());

    private static final byte[] MAGIC_TEX = new byte[]{0x54, 0x45, 0x58};
    private static final byte[] MAGIC_PNG = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] MAGIC_DDS = new byte[]{0x44, 0x44, 0x53, 0x20};
    private static final byte[] MAGIC_BMP = new byte[]{0x42, 0x4D};

    private final String filename;
    private TextureType type = TextureType.UNKNOWN;
    private int textureSize;
    private int width;
    private int height;
    private final int frameRate;
    private final int numFrames;
    private final byte[] md5sum;

    public Texture(String filename, byte[] data) {
        super(filename, data, ResourceType.TEXTURE);
        this.filename = filename;

        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Texture data can't be empty");
        }

        try {
            this.md5sum = MessageDigest.getInstance("MD5").digest(getData());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

        byte[] containerMagic = Arrays.copyOf(data, 4);

        int frames = 0;
        int frate = 0;

        if (Arrays.compare(containerMagic, 0, MAGIC_TEX.length, MAGIC_TEX, 0, MAGIC_TEX.length) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            int textureVersion = buffer.get(3);
            int ddsOffset;
            frate = buffer.getInt(4);
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
                throw new IllegalStateException("invalid tex version:" + textureVersion + "; file:" + filename);
            }
            height = buffer.getInt(ddsOffset + 12);
            width = buffer.getInt(ddsOffset + 16);

            if (textureSize <= 0 || textureSize > data.length) {
                throw new IllegalStateException("invalid texture size");
            }

            // count the number of frames in the TEX
            buffer.position(ddsOffset - 4);
            int next = buffer.position();
            if (frate > 0) {
                do {
                    buffer.position(next);
                    frames++;
                    next = buffer.position() + buffer.getInt() + 4;
                } while (next < buffer.capacity());
            }
            buffer.clear();
        } else if (Arrays.compare(containerMagic, 0, 3, MAGIC_DDS, 0, 3) == 0) {
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(4);
            int ddsHeaderStructSize = buffer.getInt();
            int ddsHeaderFlags = buffer.getInt();
            int pixelFormatSize = buffer.getInt(76);

            if (ddsHeaderStructSize == 124 && pixelFormatSize == 32) {
                type = TextureType.DDS;
                textureSize = data.length;
                if ((ddsHeaderFlags & DDSHeader.Flags.DDSD_HEIGHT) == DDSHeader.Flags.DDSD_HEIGHT) {
                    height = buffer.getInt(12);
                }
                if ((ddsHeaderFlags & DDSHeader.Flags.DDSD_WIDTH) == DDSHeader.Flags.DDSD_WIDTH) {
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

                logger.log(DEBUG, "PNG bitdepth:{0}; chunkType:{1}; ihdrSize:{2}; width:{3}; height:{4};",
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
        } else {
            throw new TextureParseException("Invalid texture");
        }

        this.numFrames = frames;
        this.frameRate = frate;
    }

    public TextureType getTextureType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureSize() {
        return textureSize;
    }

    public int getFramerate() {
        return frameRate;
    }

    public int getNumFrames() {
        return numFrames;
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
