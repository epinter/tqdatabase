/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import br.com.pinter.tqdatabase.models.DbRecord;

public class CacheDbRecord extends Cache<String, DbRecord> {
    private static final Object lock = new Object();
    private static CacheDbRecord instance;

    public static CacheDbRecord getInstance() {
        CacheDbRecord c = instance;
        if (c == null) {
            synchronized (lock) {
                c = instance;
                if (c == null) {
                    c = new CacheDbRecord();
                    instance = c;
                }
            }
        }
        return instance;
    }

    private CacheDbRecord() {

    }

}
