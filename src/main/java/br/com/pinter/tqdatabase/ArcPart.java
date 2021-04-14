/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

class ArcPart {
    private int fileOffset;
    private int compressedSize;
    private int realSize;

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
}
