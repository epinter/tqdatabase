/*
 * Copyright (C) 2020 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.models.DbRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
    private final List<ArzFile> arzFiles;

    public DatabaseReader(String[] fileNames) throws IOException {
        List<ArzFile> arzFiles = new ArrayList<>();
        for (String f : fileNames) {
            if (f != null)
                arzFiles.add(new ArzFile(f));
        }

        this.arzFiles = arzFiles;
    }

    /**
     * Returns a specific {@link DbRecord}
     *
     * @param recordPath The path to read the record from.
     * @return A {@link DbRecord} instance
     */
    public DbRecord getRecord(String recordPath) {
        DbRecord record = null;
        for (ArzFile arzFile : arzFiles) {
            if (arzFile.exists(recordPath)) {
                record = arzFile.getRecord(recordPath);
            }
        }
        return record;
    }

    /**
     * Remove <b><code>recordId</code></b> from cache
     *
     * @param recordId The record-path to remove from cache
     */
    public void invalidateCacheEntry(String recordId) {
        for (ArzFile arzFile : arzFiles) {
            arzFile.invalidateCacheEntry(recordId);
        }
    }

    /**
     * Test if the record-path exists
     *
     * @param recordId The record to test
     * @return <b><code>true</code></b> if exists, <b><code>false</code></b> if not
     */
    public boolean recordExists(String recordId) {
        for (ArzFile arzFile : arzFiles) {
            if (arzFile.exists(recordId)) {
                return true;
            }
        }
        return false;
    }
}
