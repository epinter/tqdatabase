/*
 * Copyright (C) 2020 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;

import java.io.IOException;

public class DatabaseReader {
    private final ArzFile arzFile;

    public DatabaseReader(String fileName) throws IOException {
        this.arzFile = new ArzFile(fileName);
    }

    /**
     * Returns a specific {@link DbRecord}
     *
     * @param recordPath The path to read the record from.
     * @return A {@link DbRecord} instance
     */
    public DbRecord getRecord(String recordPath) {
        return arzFile.getRecord(recordPath);
    }

    /**
     * Remove <b><code>recordId</code></b> from cache
     *
     * @param recordId The record-path to remove from cache
     */
    public void invalidateCacheEntry(String recordId) {
        arzFile.invalidateCacheEntry(recordId);
    }

    /**
     * Test if the record-path exists
     *
     * @param recordId The record to test
     * @return <b><code>true</code></b> if exists, <b><code>false</code></b> if not
     */
    public boolean recordExists(String recordId) {
        return arzFile.exists(recordId);
    }
}
