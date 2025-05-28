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
import br.com.pinter.tqdatabase.dxwrapper.DxTexWrapper;
import br.com.pinter.tqdatabase.models.Texture;
import br.com.pinter.tqdatabase.models.TextureType;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

import static br.com.pinter.tqdatabase.models.TextureType.DDS;
import static br.com.pinter.tqdatabase.models.TextureType.TEXV1;
import static br.com.pinter.tqdatabase.models.TextureType.TEXV2;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

public class TextureConverter {
    private static final System.Logger logger = System.getLogger(TextureConverter.class.getName());
    private final Texture texture;
    private static final byte[] MAGIC_TEX = new byte[]{0x54, 0x45, 0x58};
    private static final byte[] MAGIC_DDS = new byte[]{0x44, 0x44, 0x53, 0x20};
    private static final String ERROR_MESSAGE = "conversion from '%s' to '%s' is not implemented";

    public TextureConverter(Texture texture) {
        this.texture = texture;
        if (EnumSet.of(TEXV1, TEXV2).contains(texture.getTextureType()) && texture.getNumFrames() > 0) {
            throw new TextureParseException("Conversion of textures with multiple frames is not supported: " + texture.getName());
        }
    }

    public Texture convert(TextureType to, boolean ignoreMipmaps) {
        TextureType type = texture.getTextureType();
        String filename = texture.getName();

        if (type == to) {
            throw new IllegalArgumentException("Invalid texture type, source and destination types are equal");
        }

        if (to.equals(DDS)) {
            switch (type) {
                case TEXV1, TEXV2 -> {
                    return new Texture(filename, convertTexHeaderToDds(ignoreMipmaps));
                }
                case PNG -> {
                    return new Texture(filename,
                            DxTexWrapper.convertPNGtoDDS(texture.getData(), DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA));
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TEXV1)) {
            switch (type) {
                case DDS -> {
                    return new Texture(filename, convertDdsHeaderToTex(TEXV1));
                }
                case PNG -> {
                    //TEXv1 doesn't have DX10 extension header
                    return new TextureConverter(new Texture(filename, DxTexWrapper.convertPNGtoDDS(texture.getData(),
                            DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA))).convert(TEXV1, ignoreMipmaps);
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TEXV2)) {
            switch (type) {
                case DDS -> {
                    return new Texture(filename, convertDdsHeaderToTex(TEXV2));
                }
                case PNG -> {
                    //TEXv2 doesn't have DX10 extension header
                    return new TextureConverter(new Texture(filename, DxTexWrapper.convertPNGtoDDS(texture.getData(),
                            DxTexWrapper.Compat.DX9, DxTexWrapper.ColorSpace.BGRA))).convert(TEXV2, ignoreMipmaps);
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        } else if (to.equals(TextureType.PNG)) {
            switch (type) {
                case TEXV1, TEXV2 -> {
                    return new TextureConverter(new Texture(filename, convertTexHeaderToDds(ignoreMipmaps)))
                            .convert(TextureType.PNG, ignoreMipmaps);
                }
                case DDS -> {
                    return new Texture(filename, DxTexWrapper.convertDDStoPNG(texture.getData()));
                }
                default -> throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
            }
        }
        throw new NotImplementedException(String.format(ERROR_MESSAGE, type, to));
    }

    private byte[] convertDdsHeaderToTex(TextureType version) {
        TextureType type = texture.getTextureType();
        String filename = texture.getName();

        if ((type.equals(TEXV1) || type.equals(TEXV2))) {
            throw new IllegalArgumentException("texture doesn't need conversion");
        }

        int ddsOffset;
        byte byteVersion;
        int ddsTextureSize = texture.getData().length;
        if (version.equals(TEXV1)) {
            ddsOffset = 12;
            byteVersion = 0x01;
        } else if (version.equals(TEXV2)) {
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
        if (version.equals(TEXV2)) {
            //alpha channel ?
            preamble.put((byte) 0);
        }
        preamble.putInt(ddsTextureSize);
        preamble.position(0);

        ByteBuffer tex = ByteBuffer.allocate(preamble.limit() + texture.getData().length).order(ByteOrder.LITTLE_ENDIAN)
                .put(preamble).put(texture.getData()).position(preamble.limit());

        //assume the DDS is not reversed
        tex.put(MAGIC_DDS);

        logger.log(DEBUG, "filename:{0}; size:{1}; textureSize:{2}; texVersion:{3}; ddsOffset:{4};",
                filename, tex.capacity(), texture.getTextureSize(), type, ddsOffset);

        //header
        DDSHeader ddsHeader;
        try {
            ddsHeader = new DDSHeader(tex.slice(ddsOffset + 4, 124).order(ByteOrder.LITTLE_ENDIAN));
        } catch (TextureParseException e) {
            logger.log(ERROR, "Error parsing texture {0}", e);
            throw e;
        }

        ddsHeader.zeroRgbBitMasks();

        //unknown byte on texv2, many textures without alpha channel have it set to 0, textures with alpha have it set to 1
        if (version == TEXV2) {
            tex.put(texFlagOffset, (byte) (ddsHeader.getPfDwRGBBitCount() == 32 ? 1 : 0));
        }
        return tex.array();
    }

    private byte[] convertTexHeaderToDds(boolean ignoreMipmaps) {
        TextureType type = texture.getTextureType();
        String filename = texture.getName();

        if (!(type.equals(TEXV1) || type.equals(TEXV2))) {
            throw new IllegalArgumentException("texture doesn't need conversion");
        }

        ByteBuffer tex = ByteBuffer.wrap(texture.getData()).order(ByteOrder.LITTLE_ENDIAN);
        tex.position(0);

        int ddsOffset;
        if (type == TEXV1) {
            ddsOffset = 12;
        } else //noinspection ConstantValue
            if (type == TEXV2) {
                ddsOffset = 13;
            } else {
                throw new UnsupportedOperationException("texture type not suppored");
            }
        tex.position(ddsOffset);

        if (texture.getTextureSize() <= 0 || texture.getTextureSize() > tex.capacity()) {
            throw new IllegalStateException("invalid texture size");
        }

        logger.log(DEBUG, "filename:{0}; size:{1}; texVersion:{2}; ddsOffset:{3};",
                filename, tex.capacity(), type, ddsOffset);

        boolean reverseMips = tex.get(ddsOffset + 3) == 0x52;

        //replace with correct magic
        tex.put(MAGIC_DDS);

        //header
        DDSHeader ddsHeader;
        try {
            ddsHeader = new DDSHeader(tex.slice(ddsOffset + 4, 124).order(ByteOrder.LITTLE_ENDIAN));
        } catch (TextureParseException e) {
            logger.log(ERROR, "Error parsing texture {0}", e);
            throw e;
        }

        ddsHeader.calculatePitch();
        ddsHeader.fixHeader();
        ddsHeader.setRgbBitMasks();

        logger.log(DEBUG, "xxxxxxxxx " + ddsHeader.getSurfaceSize());
        byte[] ret;
        if (reverseMips && !ignoreMipmaps) {
            ByteBuffer buf = ByteBuffer.allocate(texture.getTextureSize()).order(ByteOrder.LITTLE_ENDIAN);
            buf.put(0, tex, ddsOffset, 128);
            buf.put(128, tex, (ddsOffset + texture.getTextureSize()) - ddsHeader.getSurfaceSize(), ddsHeader.getSurfaceSize());

            buf.position(128 + ddsHeader.getSurfaceSize());

            //skip the main surface
            int mipPos = (ddsOffset + texture.getTextureSize()) - ddsHeader.getSurfaceSize();

            //get all mipmaps starting from the end of the .TEX
            for (int i = 1, mipWidth = Math.max(ddsHeader.getDwWidth() / 2, 1), mipHeight = Math.max(ddsHeader.getDwHeight() / 2, 1);
                 i < ddsHeader.getDwMipMapCount();
                 i++, mipWidth = Math.max(mipWidth / 2, 1), mipHeight = Math.max(mipHeight / 2, 1)) {
                int size = ddsHeader.calculateMipMapSize(mipWidth, mipHeight);
                mipPos -= size;

                logger.log(DEBUG, "i:{0}; buf:{1}; pos={2}; size:{3}; w:{4}; h:{5};",
                        i, buf.capacity(), buf.position(), size, mipWidth, mipHeight);

                buf.put(tex.slice(mipPos, size));
                if (mipWidth == 1 && mipHeight == 1) {
                    break;
                }
            }

            //check if we returned to position 128
            if (ddsHeader.getDwMipMapCount() > 1 && (mipPos - ddsOffset != 128 || (buf.position() != texture.getTextureSize()))) {
                throw new TextureParseException("Total output mipmaps length doesn't match the input: file:'%s'; out:%s; tex:%s"
                        .formatted(filename, buf.position(), mipPos - 12));
            }
            ret = new byte[buf.capacity()];
            buf.rewind();
            buf.get(ret);
        } else {
            if (ignoreMipmaps) {
                int index = reverseMips ? tex.capacity() - ddsHeader.getSurfaceSize() : ddsOffset + 128;
                ddsHeader.setDwMipMapCount(1);
                ret = new byte[128 + ddsHeader.getSurfaceSize()];
                tex.get(ddsOffset, ret, 0, 128);
                tex.get(index, ret, 128, ddsHeader.getSurfaceSize());
            } else {
                ret = new byte[texture.getTextureSize()];
                tex.get(ddsOffset, ret, 0, ret.length);
            }
        }
        tex.clear();
        return ret;
    }
}
