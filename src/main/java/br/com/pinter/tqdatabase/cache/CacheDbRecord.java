/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import br.com.pinter.tqdatabase.models.DbRecord;

public class CacheDbRecord extends Cache<String, DbRecord> {
    private static final Object lock = new Object();
    private static volatile CacheDbRecord _instance;

    public static CacheDbRecord getInstance() {
        CacheDbRecord c = _instance;
        if (c == null) {
            synchronized (lock) {
                c = _instance;
                if (c == null) {
                    c = new CacheDbRecord();
                    _instance = c;
                }
            }
        }
        return _instance;
    }

    private CacheDbRecord() {

    }

}
