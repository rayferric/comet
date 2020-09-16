package com.rayferric.comet.core.util;

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
        return ((running ? System.nanoTime() : endTime) - startTime) * INVERSE_FREQUENCY;
    }

    private static final double INVERSE_FREQUENCY = 1e-9;

    private boolean running;
    private long startTime, endTime;
}
