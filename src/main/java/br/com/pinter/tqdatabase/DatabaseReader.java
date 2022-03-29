/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase;

import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
    private final List<ArzFile> arzFiles;
    private boolean useCache;

    public DatabaseReader(String[] fileNames) throws IOException {
        this(fileNames, true);
    }

    public DatabaseReader(String[] fileNames, boolean useCache) throws IOException {
        this.useCache = useCache;
        List<ArzFile> files = new ArrayList<>();
        for (String f : fileNames) {
            if (f != null)
                files.add(new ArzFile(f));
        }

        this.arzFiles = files;
    }

    /**
     * Returns a specific {@link DbRecord}
     *
     * @param recordPath The path to read the record from.
     * @return A {@link DbRecord} instance
     */
    public DbRecord getRecord(String recordPath) {
        DbRecord record = null;
        String normPath = Util.normalizeRecordPath(recordPath);

        if (useCache && CacheDbRecord.getInstance().containsKey(normPath)) {
            return CacheDbRecord.getInstance().get(normPath);
        }

        for (ArzFile arzFile : arzFiles) {
            if (arzFile.exists(recordPath)) {
                record = arzFile.getRecord(recordPath);
            }
        }

        if (record != null && useCache) {
            CacheDbRecord.getInstance().put(normPath, record);
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
