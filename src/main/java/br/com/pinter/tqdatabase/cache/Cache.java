/*
 * Copyright (C) 2019 Emerson Pinter - All Rights Reserved
 */

package br.com.pinter.tqdatabase.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"UnusedReturnValue", "SuspiciousMethodCalls"})
abstract class Cache<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();

    public V get(K key) {
        return cache.get(key);
    }

    public V put(K key, V value) {
        return cache.put(key, value);
    }

    public V remove(K key) {
        return cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean remove(K key, V value) {
        return cache.remove(key, value);
    }

    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
