/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class Cache<K, V> {
    private final Map<K, V> data = new ConcurrentHashMap<>();

    public V get(K key) {
        return data.get(key);
    }

    public void put(K key, V value) {
        data.put(key, value);
    }

    public void remove(K key) {
        data.remove(key);
    }

    public void clear() {
        data.clear();
    }

    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
