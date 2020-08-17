package com.rayferric.comet.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class AutoMap<T> {
    public AutoMap() {
        map = new ConcurrentHashMap<>();
        idGen = new AtomicLong(0);
    }

    public AutoMap(AutoMap<T> other) {
        map = new ConcurrentHashMap<>(other.map);
        idGen = new AtomicLong(other.idGen.get());
    }

    public T get(long handle) {
        Optional<T> optional = map.get(handle);
        if(optional != null)
            return optional.orElse(null);
        else
            return null;
    }

    public long put(T value) {
        long key = idGen.getAndIncrement();
        if(value != null)
            map.put(key, Optional.of(value));
        else
            map.put(key, Optional.empty());
        return key;
    }

    public T remove(long handle) {
        Optional<T> optional = map.remove(handle);
        if(optional != null)
            return optional.orElse(null);
        else
            return null;
    }

    public void forEach(BiConsumer<Long, T> consumer) {
        map.forEach((handle, value) -> {
            consumer.accept(handle, value.orElse(null));
        });
    }

    private ConcurrentHashMap<Long, Optional<T>> map;
    private AtomicLong idGen;
}
