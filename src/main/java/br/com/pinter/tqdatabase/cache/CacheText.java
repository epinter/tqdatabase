/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;


public class CacheText extends Cache<String, String> {
    private CacheText() {
    }

    private static class SingletonHolder {
        private static final CacheText instance = new CacheText();
    }

    public static CacheText getInstance() {
        return SingletonHolder.instance;
    }
}
