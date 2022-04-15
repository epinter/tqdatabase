/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import br.com.pinter.tqdatabase.models.DbRecord;

public class CacheDbRecord extends Cache<String, DbRecord> {
    private CacheDbRecord() {
    }

    private static class SingletonHolder {
        private static final CacheDbRecord instance = new CacheDbRecord();
    }

    public static CacheDbRecord getInstance() {
        return SingletonHolder.instance;
    }
}
