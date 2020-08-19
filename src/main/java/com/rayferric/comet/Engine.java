package com.rayferric.comet;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.server.VideoServer;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.Window;
import com.rayferric.comet.video.common.VideoAPI;
import com.rayferric.comet.video.gl.GLVideoEngine;
import com.rayferric.comet.video.gl.GLWindow;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class Engine {
    @Override
    public String toString() {
        return String.format("Engine{videoServer=%s, threadPool=%s}", videoServer, threadPool);
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
     * @param title            window title
     * @param videoApi         video API
     * @param loaderThreads    number of resource (un)loading threads
     * @param jobThreads       number of job processing threads
     */
    public void start(String title, VideoAPI videoApi, int loaderThreads, int jobThreads) {
        if(!started.compareAndSet(false, true))
            throw new IllegalStateException("Attempted to start an already started engine.");

        final Window window;
        final VideoEngine videoEngine;
        if(videoApi == VideoAPI.OPENGL) {
            window = new GLWindow(title, new Vector2i(640, 360));
            videoEngine = new GLVideoEngine(window.getFramebufferSize());
        } else
            throw new IllegalArgumentException("Requested use of non-existent API.");

        videoServer.set(new VideoServer(window, videoEngine));
        getVideoServer().start();

        loaderPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(loaderThreads));
        threadPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(jobThreads));
    }

    /**
     * Stops the engine and makes it unable to run until next call to {@link #start}.<br>
     * This method stops thread pool and servers, finally, destroys the window.<br>
     * All getters will return null from now on.<br>
     * Unloads all resources ever created.
     */
    public void stop() {
        if(!started.compareAndSet(true, false))
            throw new IllegalStateException("Attempted to stop an already stopped engine.");

        getThreadPool().shutdown();
        getLoaderPool().shutdown();
        try {
            getThreadPool().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            getLoaderPool().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        // Wait for server creation queues:
        try {
            getVideoServer().waitForCreationQueue();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        // Unload resources:
        for(Resource resource : new ArrayList<>(resources))
            resource.unload();

        // Wait for server destruction queues and stop:
        try {
            getVideoServer().waitForDestructionQueue();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        getVideoServer().stop();

        // Now the servers are fully terminated.
        // Resource list is also cleared.

        getVideoServer().getWindow().destroy();

        videoServer.set(null);
        loaderPool.set(null);
        threadPool.set(null);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }

    public VideoAPI getVideoApi() {
        return videoApi.get();
    }

    /**
     * Sets video API and resets video server.
     * All video resources are reloaded.
     * The window will be reopened too.
     *
     * @param api    new API
     */
    public void changeVideoApi(VideoAPI api) {
        videoApi.set(api);

        VideoServer server = getVideoServer();

        try {
            server.waitForCreationQueue();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        List<Resource> videoResources = new ArrayList<>(resources.size());
        synchronized(resources) {
            for(Resource resource : new ArrayList<>(resources))
                if(resource instanceof VideoResource)
                    videoResources.add(resource);
        }

        for(Resource resource : videoResources)
            resource.unload();

        try {
            getVideoServer().waitForDestructionQueue();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();

        Window oldWindow = server.getWindow();
        server.setWindow(new GLWindow(oldWindow));
        oldWindow.destroy();
        server.setVideoEngine(new GLVideoEngine(server.getVideoEngine()));

        server.start();

        for(Resource resource : videoResources)
            resource.load();
    }

    /**
     * Loops until {@link #exit()} is called.<br>
     * Closing the window while in this loop will call {@link #exit()}. (TODO Remove that feature, leave to the user)
     */
    public void run() {

        shouldExit.set(false);
        while(!shouldExit.get()) {
            Window.pollEvents();

            if(getVideoServer().getWindow().shouldClose()) exit(); // Example of use in a script

            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Schedules termination of the main loop.
     */
    public void exit() {
        shouldExit.set(true);
    }

    // <editor-fold desc="Getters">

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
     * Returns resource (un)loading thread pool sub-resource.<br>
     * Is null if the engine is stopped.
     *
     * @return thread pool
     */
    public ThreadPoolExecutor getLoaderPool() {
        return loaderPool.get();
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

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicReference<VideoAPI> videoApi = new AtomicReference<>(VideoAPI.OPENGL);
    private final List<Resource> resources = Collections.synchronizedList(new ArrayList<>());
    private final AtomicReference<VideoServer> videoServer = new AtomicReference<>(null);

    private final AtomicReference<ThreadPoolExecutor> loaderPool = new AtomicReference<>(null);
    private final AtomicReference<ThreadPoolExecutor> threadPool = new AtomicReference<>(null);

    private final AtomicBoolean shouldExit = new AtomicBoolean(false);

    private Engine() {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW.");
    }
}
