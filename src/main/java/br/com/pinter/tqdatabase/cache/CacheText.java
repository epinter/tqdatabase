/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;


public class CacheText extends Cache<String, String> {
    private static final Object lock = new Object();
    private static CacheText instance;

    public static CacheText getInstance() {
        CacheText c = instance;
        if (c == null) {
            synchronized (lock) {
                c = instance;
                if (c == null) {
                    c = new CacheText();
                    instance = c;
                }
            }
        }
        return instance;
    }

    private CacheText() {

    }

}
