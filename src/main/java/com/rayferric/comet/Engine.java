package com.rayferric.comet;

import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.video.VideoServer;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    /**
     * Returns text representation of the current state of the engine.<br>
     * • May be called from any thread.
     *
     * @return current state in text format
     */
    @Override
    public String toString() {
        return String
                .format("Engine{running=%s, resources=%s, videoServer=%s, loaderPool=%s, threadPool=%s}", running.get(),
                        resources, videoServer, loaderPool, jobPool);
    }

    /**
     * Returns engine singleton instance.<br>
     * • May be called from any thread.
     *
     * @return engine instance
     */
    public static Engine getInstance() {
        return INSTANCE;
    }

    // <editor-fold desc="Core API">

    /**
     * Loads libraries, creates engine resources, and starts all core threads.<br>
     * • May be used to reinitialize after {@link #stop()}.<br>
     * • The engine is running from now on and must either enter the {@link #run main loop} or be {@link #stop() stopped}.<br>
     * • Must be called before creating any {@link Resource resources}.<br>
     * • Must only be called from the main thread.
     *
     * @param info engine configuration, must not be null
     */
    public void start(EngineInfo info) {
        if(!running.compareAndSet(false, true))
            throw new IllegalStateException("Attempted to start an already started engine.");

        // Load libraries:
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW.");

        // Create servers:
        videoServer.set(new VideoServer(info));

        // Create thread pools:
        loaderPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(info.getLoaderThreads()));
        jobPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(info.getJobThreads()));

        // Start the servers:
        getVideoServer().start();
    }

    /**
     * Halts all core threads and stops the engine, finally unloads libraries.
     * Makes the engine unusable until next call to {@link #start}.<br>
     * • Automatically unloads all {@link Resource resources} created while the engine was running.<br>
     * • Must only be called from the main thread.
     */
    public void stop() {
        if(!running.compareAndSet(true, false))
            throw new IllegalStateException("Attempted to stop an already stopped engine.");

        // Shut down thread pools:
        getJobPool().shutdown();
        getLoaderPool().shutdown();
        try {
            getJobPool().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            getLoaderPool().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Wait for server creation queues:
        getVideoServer().waitForCreationQueue();

        // Unload resources:
        for(Resource resource : new ArrayList<>(resources))
            resource.unload();

        // Wait for server destruction queues:
        getVideoServer().waitForDestructionQueue();

        // Stop the servers:
        getVideoServer().stop();

        // Destroy the servers:
        getVideoServer().destroy();

        // Unload libraries:
        glfwTerminate();
    }

    /**
     * Processes system events.<br>
     * • Must be systematically called for the process to keep responding.<br>
     * • Is called from the {@link #run(Runnable)  main loop}.<br>
     * • Must only be called from the main thread.
     */
    public void process() {
        glfwPollEvents();
    }

    /**
     * Makes the window visible and enters the main loop, hides the window upon finalization.<br>
     * • Systematically issues calls to {@link #process()}, as required.<br>
     * • Finalized by calling {@link #exit()} from any thread.<br>
     * • Must only be called from the main thread.
     *
     * @param iteration runnable to be called every iteration, must not be null
     */
    public void run(Runnable iteration) {
        getVideoServer().getWindow().setVisible(true);
        shouldExit.set(false);
        while(!shouldExit.get()) {
            process();
            iteration.run();
        }
        getVideoServer().getWindow().setVisible(false);
    }

    // </editor-fold>

    // <editor-fold desc="Parallel API">

    /**
     * Requests termination of the {@link #run(Runnable) main loop}.<br>
     * • May be called from any thread.
     */
    public void exit() {
        shouldExit.set(true);
    }

    public <T> void reloadResources(Class<T> type) {
        List<Resource> snapshot;
        synchronized(resources) {
            snapshot = new ArrayList<>(resources);
        }
        for(Resource resource : snapshot) {
            if(type.isAssignableFrom(resource.getClass()))
                resource.reload();
        }
    }

    // </editor-fold>

    // <editor-fold desc="Internal API">

    /**
     * Returns a snapshot of a {@link ArrayList list} of currently loaded {@link Resource resources}.<br>
     * • Is internally used by the servers.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @return a snapshot of the resource registry
     */
    public List<Resource> snapLoadedResources() {
        synchronized(resources) {
            return new ArrayList<Resource>(resources);
        }
    }

    /**
     * Registers a {@link Resource}.<br>
     * • Is internally called by {@link Resource} when it has finished loading.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param resource resource, must not be null
     */
    public void registerLoadedResource(Resource resource) {
        resources.add(resource);
    }

    /**
     * Unregisters a {@link Resource}.<br>
     * • Is internally called by {@link Resource} when it has started unloading.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param resource resource, must not be null
     */
    public void unregisterUnloadedResource(Resource resource) {
        resources.remove(resource);
    }

    // </editor-fold>

    // <editor-fold desc="Getters">

    /**
     * Returns the video server.<br>
     * • May be called from any thread.
     *
     * @return server
     */
    public VideoServer getVideoServer() {
        return videoServer.get();
    }

    /**
     * Returns the resource loading thread pool.<br>
     * • May be called from any thread.
     *
     * @return thread pool
     */
    public ThreadPoolExecutor getLoaderPool() {
        return loaderPool.get();
    }

    /**
     * Returns the job processing thread pool.<br>
     * • May be called from any thread.
     *
     * @return thread pool
     */
    public ThreadPoolExecutor getJobPool() {
        return jobPool.get();
    }

    // </editor-fold>

    private static final Engine INSTANCE = new Engine();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shouldExit = new AtomicBoolean();

    private final List<Resource> resources = Collections.synchronizedList(new ArrayList<>());

    // Servers
    private final AtomicReference<VideoServer> videoServer = new AtomicReference<>(null);

    // Thread Pools
    private final AtomicReference<ThreadPoolExecutor> loaderPool = new AtomicReference<>(null);
    private final AtomicReference<ThreadPoolExecutor> jobPool = new AtomicReference<>(null);
}
