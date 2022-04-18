/*
 * Copyright (C) 2022 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import java.io.IOException;

public class EntryNotFoundException extends IOException {
    public EntryNotFoundException(String message) {
        super(message);
    }
}
