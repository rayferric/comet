package com.rayferric.comet.util;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicFloat extends Number {
    private final AtomicInteger bits;

    public AtomicFloat() {
        bits = new AtomicInteger();
    }

    public AtomicFloat(float initialValue) {
        bits = new AtomicInteger(Float.floatToIntBits(initialValue));
    }

    @Override
    public int intValue() {
        return bits.intValue();
    }

    @Override
    public long longValue() {
        return bits.longValue();
    }

    @Override
    public float floatValue() {
        return bits.floatValue();
    }

    @Override
    public double doubleValue() {
        return bits.doubleValue();
    }

    public float get() {
        return Float.intBitsToFloat(bits.get());
    }

    public void set(float newValue) {
        bits.set(Float.floatToIntBits(newValue));
    }
}
