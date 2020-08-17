package com.rayferric.comet.util;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AutoMap<V> extends HashMap<Long, V> {
    public V put(V value) {
        long key = idGen.getAndIncrement();
        return super.put(key, value);
    }

    private final AtomicLong idGen = new AtomicLong(0);
}
