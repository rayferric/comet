package com.rayferric.comet.server;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server {
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Server other = (Server)o;
        return Objects.equals(thread, other.thread) &&
                Objects.equals(running, other.running);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thread, running);
    }

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
