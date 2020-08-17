package com.rayferric.comet.server;

public class Server {
    public void join() {
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected Thread thread;

    protected Server(Runnable runnable) {
        thread = new Thread(runnable);
    }
}
