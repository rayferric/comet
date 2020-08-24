package com.rayferric.comet.util;

public class Timer {
    public Timer() {
        running = false;
        reset();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if(!running)
            startTime += System.nanoTime() - endTime;
        running = true;
    }

    public void stop() {
        if(running)
            endTime = System.nanoTime();
        running = false;
    }

    public void reset() {
        endTime = startTime = System.nanoTime();
    }

    public double getElapsed() {
        return (double)((running ? System.nanoTime() : endTime) - startTime) / FREQUENCY;
    }

    private static final long FREQUENCY = (long)1e+9;

    private boolean running;
    private long startTime;
    private long endTime;
}
