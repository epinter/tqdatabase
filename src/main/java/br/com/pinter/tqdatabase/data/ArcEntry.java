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

import br.com.pinter.tqdatabase.models.StorageType;

import java.util.ArrayList;
import java.util.List;

class ArcEntry {
    private final String filename;
    private final StorageType storageType;
    private final int fileOffset;
    private final int compressedSize;
    private final int realSize;
    private final List<DataBlock> parts = new ArrayList<>();

    ArcEntry(String filename, StorageType storageType, int fileOffset, int compressedSize, int realSize) {
        this.filename = filename;
        this.storageType = storageType;
        this.fileOffset = fileOffset;
        this.compressedSize = compressedSize;
        this.realSize = realSize;
    }

    String getFilename() {
        return filename;
    }

    StorageType getStorageType() {
        return storageType;
    }

    int getFileOffset() {
        return fileOffset;
    }

    int getCompressedSize() {
        return compressedSize;
    }

    int getRealSize() {
        return realSize;
    }

    List<DataBlock> getParts() {
        return parts;
    }

    void addPart(int index, DataBlock part) {
        this.parts.add(index, part);

    }

    boolean isCompressed() {
        return storageType.equals(StorageType.COMPRESSED);
    }
}
