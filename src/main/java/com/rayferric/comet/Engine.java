package com.rayferric.comet;

import com.rayferric.comet.server.VideoServer;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.display.Window;
import com.rayferric.comet.video.gl.GLVideoEngine;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Engine {
    public enum VideoAPI {
        OPENGL
    }

    public Engine(int numThreads, VideoAPI videoApi, String title) {
        final VideoEngine videoEngine;
        if(videoApi == VideoAPI.OPENGL) {
            window = new Window(title, 1280, 720); // This is a GL window for now
            videoEngine = new GLVideoEngine();
        } else
            throw new IllegalStateException("Requested use of non-existent API.");

        videoServer = new VideoServer(window, videoEngine);

        threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Engine other = (Engine)o;
        return Objects.equals(window, other.window) &&
                Objects.equals(videoServer, other.videoServer) &&
                Objects.equals(threadPool, other.threadPool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(window, videoServer, threadPool);
    }

    @Override
    public String toString() {
        return String.format("Engine{window=%s, videoServer=%s, threadPool=%s}", window, videoServer, threadPool);
    }

    public void run() {
        while(window.isOpen()) {
            Window.pollEvents();

            if(window.shouldClose()) stop(); // Example of use in a script

            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        // We must shut down the thread pool before the servers,
        // as off-thread resource loaders may still push new recipes onto the queues
        threadPool.shutdownNow();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        videoServer.stop();

        window.close();
    }

    public Window getWindow() {
        return window;
    }

    public VideoServer getVideoServer() {
        return videoServer;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    private final Window window;
    private final VideoServer videoServer;
    private final ThreadPoolExecutor threadPool;
}
