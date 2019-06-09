/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import java.util.ArrayList;

class ArcEntry {
    private int storageType;
    private int fileOffset;
    private int compressedSize;
    private int realSize;
    private ArrayList<ArcPart> parts;

    int getStorageType() {
        return storageType;
    }

    void setStorageType(int storageType) {
        this.storageType = storageType;
    }

    int getFileOffset() {
        return fileOffset;
    }

    void setFileOffset(int fileOffset) {
        this.fileOffset = fileOffset;
    }

    int getCompressedSize() {
        return compressedSize;
    }

    void setCompressedSize(int compressedSize) {
        this.compressedSize = compressedSize;
    }

    int getRealSize() {
        return realSize;
    }

    void setRealSize(int realSize) {
        this.realSize = realSize;
    }

    ArrayList<ArcPart> getParts() {
        return parts;
    }

    void setParts(ArrayList<ArcPart> parts) {
        this.parts = parts;
    }

    boolean isActive() {
        if (this.getStorageType() == 1) {
            return true;
        } else {
            return this.getParts() != null;
        }
    }

}
