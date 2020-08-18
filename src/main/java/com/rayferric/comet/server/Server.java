package com.rayferric.comet.server;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server {
    @Override
    public String toString() {
        return String.format("Server{thread=%s, running=%s}", thread, running);
    }

    /**
     * Requests server thread termination and waits for it to shut down.
     */
    public void stop() {
        running.set(false);
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    protected Thread thread;
    protected final AtomicBoolean running = new AtomicBoolean(true);
}
