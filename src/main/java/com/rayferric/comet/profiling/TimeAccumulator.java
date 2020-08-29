package com.rayferric.comet.profiling;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimeAccumulator {
    public TimeAccumulator(int samples) {
        this.samplesInv = 1.0 / samples;
    }

    public void accumulate(double time) {
        try {
            lock.writeLock().lock();

            avg += (time - avg) * samplesInv;

            if(time < avg) low += (time - low) * samplesInv;
            else high += (time - high) * samplesInv;

            if(time < low) min += (time - min) * samplesInv;
            else if(time > high) max += (time - max) * samplesInv;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public double getMin() {
        try {
            lock.readLock().lock();
            return min;
        } finally {
            lock.readLock().unlock();
        }
    }

    public double getAvg() {
        try {
            lock.readLock().lock();
            return avg;
        } finally {
            lock.readLock().unlock();
        }
    }

    public double getMax() {
        try {
            lock.readLock().lock();
            return max;
        } finally {
            lock.readLock().unlock();
        }
    }

    private final double samplesInv;
    private double min = 0, low = 0, avg = 0, high = 0, max = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}
