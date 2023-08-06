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

import br.com.pinter.tqdatabase.cache.CacheDbRecord;
import br.com.pinter.tqdatabase.models.DbRecord;
import br.com.pinter.tqdatabase.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
    private final List<ArzFile> arzFiles;
    private final boolean useCache;

    public DatabaseReader(String[] fileNames) throws IOException {
        this(fileNames, true);
    }

    public DatabaseReader(String[] fileNames, boolean useCache) throws IOException {
        if(fileNames.length == 0) {
            throw new IOException("no database found");
        }
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
