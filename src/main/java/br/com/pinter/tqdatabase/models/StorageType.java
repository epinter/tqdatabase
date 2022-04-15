/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.models;

import java.io.IOException;
import java.util.Arrays;

public enum StorageType {
    UNCOMPRESSED(1),
    COMPRESSED(3);

    private final int value;

    StorageType(int value) {
        this.value = value;
    }

    public static StorageType valueOf(int storageType) throws IOException {
        return Arrays.stream(StorageType.values()).filter(v -> v.value == storageType)
                .findFirst().orElseThrow(() -> new IOException("StorageType not implemented: "+storageType));
    }
}
