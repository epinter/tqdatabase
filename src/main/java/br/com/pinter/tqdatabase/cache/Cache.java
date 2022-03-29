/*
 * Copyright (C) 2021 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"UnusedReturnValue", "SuspiciousMethodCalls"})
abstract class Cache<K, V> {
    private final Map<K, V> data = new ConcurrentHashMap<>();

    public V get(K key) {
        return data.get(key);
    }

    public V put(K key, V value) {
        return data.put(key, value);
    }

    public V remove(K key) {
        return data.remove(key);
    }

    public void clear() {
        data.clear();
    }

    public boolean remove(K key, V value) {
        return data.remove(key, value);
    }

    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
