package com.rayferric.comet;

import com.rayferric.comet.video.common.VideoEngine;
import com.rayferric.comet.video.display.Window;
import com.rayferric.comet.video.gl.GLVideoEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Engine {
    public enum VideoAPI {
        OPENGL
    }

    public Engine(int numThreads, VideoAPI videoApi, String title) {
        threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
        if(videoApi == VideoAPI.OPENGL)videoEngine = new GLVideoEngine(title, 1280, 720);
        else throw new IllegalStateException("Requested use of non-existent video API.");
    }

    public void terminate() {
        // Halt all enqueued tasks and wait for the currently executed ones to finish
        threadPool.shutdownNow();
        // Waiting for thread pool termination is necessary, as there are semaphores acquired by both
        // The rendering (video server) thread and current thread pool tasks
        // This logic effectively prevents a deadlock
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        videoEngine.stopAndFreeResources();
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public VideoEngine getVideoEngine() {
        return videoEngine;
    }

    private final ThreadPoolExecutor threadPool;
    private final VideoEngine videoEngine;
}
