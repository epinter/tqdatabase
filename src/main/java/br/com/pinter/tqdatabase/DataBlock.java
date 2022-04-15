/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

record DataBlock(int fileOffset, int compressedSize, int realSize) {
}
