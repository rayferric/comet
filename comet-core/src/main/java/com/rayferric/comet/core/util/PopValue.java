package com.rayferric.comet.core.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class PopValue <T> {
    public PopValue(T defaultValue) {
        value = new AtomicReference<>(defaultValue);
    }

    public T get() {
        return value.get();
    }

    public void set(T value) {
        this.value.set(value);
        modified.set(true);
    }

    public void modify(UnaryOperator<T> operator) {
        value.updateAndGet(operator);
    }

    public boolean popModified() {
        return modified.getAndSet(false);
    }

    AtomicReference<T> value;
    AtomicBoolean modified = new AtomicBoolean(true);
}
