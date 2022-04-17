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
