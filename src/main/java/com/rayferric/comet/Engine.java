package com.rayferric.comet;

import com.rayferric.comet.server.VideoServer;
import com.rayferric.comet.video.common.VideoEngine;
import com.rayferric.comet.video.common.Window;
import com.rayferric.comet.video.gl.GLVideoEngine;
import com.rayferric.comet.video.gl.GLWindow;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class Engine {
    public enum VideoAPI {
        OPENGL
    }

    @Override
    public String toString() {
        return String.format("Engine{window=%s, videoServer=%s, threadPool=%s}", window, videoServer, threadPool);
    }

    /**
     * Returns engine singleton instance.
     *
     * @return instance
     */
    public static Engine getInstance() {
        return INSTANCE;
    }

    /**
     * Permanently destroys the instance, making it unable to run again.<br>
     * Call this before quitting the application.
     */
    public void destroy() {
        glfwTerminate();
    }

    /**
     * Creates all sub-resources required by the engine and starts their respective threads.<br>
     * The engine is running from now on and must enter the main loop as quickly as possible.
     *
     * @param numThreads number of thread pool workers
     * @param videoApi   video API
     * @param title      window title
     */
    public void start(int numThreads, VideoAPI videoApi, String title) {
        final VideoEngine videoEngine;
        if(videoApi == VideoAPI.OPENGL) {
            window.set(new GLWindow(title, 640, 360));
            videoEngine = new GLVideoEngine(window.get().getFramebufferSize());
        } else
            throw new RuntimeException("Requested use of non-existent API.");

        videoServer.set(new VideoServer(window.get(), videoEngine));
        videoServer.get().start();

        threadPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads));
    }

    /**
     * Stops the engine and makes it unable to run until next call to {@link #start}.<br>
     * This method stops thread pool and servers, finally, closes the window.<br>
     * All getters will return null from now on.
     */
    public void stop() {
        // We must fully shut down the thread pool before stopping the servers,
        // as off-thread resource loaders may still push new recipes onto the queues
        threadPool.get().shutdown();
        try {
            threadPool.get().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        videoServer.get().stop();

        window.get().close();

        window.set(null);
        videoServer.set(null);
        threadPool.set(null);
    }

    /**
     * Loops until {@link #exit()} is called.<br>
     * Closing the window while in this loop will call {@link #exit()}. (TODO Remove that feature)
     * {@link #stop()} will be called right after.
     */
    public void run() {
        shouldExit.set(false);
        while(!shouldExit.get()) {
            Window.pollEvents();

            if(getWindow().shouldClose()) exit(); // Example of use in a script
            getVideoServer().reloadResources(); // Actively reload resources to find multithreading errors

            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        stop();
    }

    /**
     * Schedules termination of the main loop.
     */
    public void exit() {
        shouldExit.set(true);
    }

    // <editor-fold desc="Getters">

    /**
     * Returns window sub-resource.<br>
     * Is null if the engine is stopped.
     *
     * @return window
     */
    public Window getWindow() {
        return window.get();
    }

    /**
     * Returns video server sub-resource.<br>
     * Is null if the engine is stopped.
     *
     * @return video server
     */
    public VideoServer getVideoServer() {
        return videoServer.get();
    }

    /**
     * Returns thread pool sub-resource.<br>
     * Is null if the engine is stopped.
     *
     * @return thread pool
     */
    public ThreadPoolExecutor getThreadPool() {
        return threadPool.get();
    }

    // </editor-fold>

    private static final Engine INSTANCE = new Engine();

    private final AtomicReference<Window> window = new AtomicReference<>(null);
    private final AtomicReference<VideoServer> videoServer = new AtomicReference<>(null);
    private final AtomicReference<ThreadPoolExecutor> threadPool = new AtomicReference<>(null);
    private final AtomicBoolean shouldExit = new AtomicBoolean(false);

    private Engine() {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW.");
    }
}
