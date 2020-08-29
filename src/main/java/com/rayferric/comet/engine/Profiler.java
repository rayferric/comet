package com.rayferric.comet.engine;

import com.rayferric.comet.util.AtomicFloat;

public class Profiler {
    public void setTimerSamples(int samples) {
        timerSamplesInverse.set(1F / samples);
    }

    public double getVideoCpuTime() {
        synchronized(videoCpuTimeLock) {
            return videoCpuTime;
        }
    }

    public void addVideoCpuTime(double time) {
        synchronized(videoCpuTimeLock) {
            videoCpuTime += (time - videoCpuTime) * timerSamplesInverse.get();
        }
    }

    public double getVideoGpuTime() {
        synchronized(videoGpuTimeLock) {
            return videoGpuTime;
        }
    }

    public void addVideoGpuTime(double time) {
        synchronized(videoGpuTimeLock) {
            videoGpuTime += (time - videoGpuTime) * timerSamplesInverse.get();
        }
    }

    private final AtomicFloat timerSamplesInverse = new AtomicFloat(1);
    private double videoCpuTime;
    private final Object videoCpuTimeLock = new Object();
    private double videoGpuTime;
    private final Object videoGpuTimeLock = new Object();
}
