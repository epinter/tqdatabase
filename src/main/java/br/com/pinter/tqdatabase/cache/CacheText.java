/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;


public class CacheText extends Cache<String, String> {
    private static final Object lock = new Object();
    private static volatile CacheText _instance;

    public static CacheText getInstance() {
        CacheText c = _instance;
        if (c == null) {
            synchronized (lock) {
                c = _instance;
                if (c == null) {
                    c = new CacheText();
                    _instance = c;
                }
            }
        }
        return _instance;
    }

    private CacheText() {

    }

}
