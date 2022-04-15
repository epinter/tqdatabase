/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

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

    public ArcEntry(String filename, StorageType storageType, int fileOffset, int compressedSize, int realSize) {
        this.filename = filename;
        this.storageType = storageType;
        this.fileOffset = fileOffset;
        this.compressedSize = compressedSize;
        this.realSize = realSize;
    }

    public String getFilename() {
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
