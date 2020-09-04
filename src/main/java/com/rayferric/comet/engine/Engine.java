package com.rayferric.comet.engine;

import com.rayferric.comet.audio.AudioServer;
import com.rayferric.comet.input.InputManager;
import com.rayferric.comet.physics.PhysicsServer;
import com.rayferric.comet.profiling.Profiler;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.VideoServer;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
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
     * • The engine is running from now on and must either enter the {@link #run main loop} or be {@link #stop() stopped}.<br>
     * • Must be called before using the API.<br>
     * • Must only be called from the main thread.
     *
     * @param info engine configuration, must not be null
     *
     * @throws RuntimeException if libraries could not be loaded
     */
    public void start(EngineInfo info) {
        // Load libraries:
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW.");

        // Create servers:
        videoServer.set(new VideoServer(info));
        audioServer.set(new AudioServer());
        physicsServer.set(new PhysicsServer());

        // Create thread pools:
        loaderPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(info.getLoaderThreads()));
        loaderPool.get().setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        jobPool.set((ThreadPoolExecutor)Executors.newFixedThreadPool(info.getJobThreads()));
        jobPool.get().setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // Create managers:
        resourceManager.set(new ResourceManager());
        layerManager.set(new LayerManager(info.getLayerCount()));
        profiler.set(new Profiler());
        inputManager.set(new InputManager());

        // Start the servers:
        getVideoServer().start();
        getAudioServer().start();
        getPhysicsServer().start();

        // Wait for them to initialize:
        getVideoServer().awaitInitialization();
        getAudioServer().awaitInitialization();
        getPhysicsServer().awaitInitialization();
    }

    /**
     * Halts all core threads and stops the engine, finally unloads libraries.<br>
     * • Makes the engine unusable from now on.<br>
     * • Automatically unloads all {@link Resource resources} created while the engine was running.<br>
     * • Must only be called from the main thread.
     */
    public void stop() {
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

        // Wait for server creation queues when loader pool is shut down:
        getVideoServer().waitForCreationQueue();
        getAudioServer().waitForCreationQueue();
        getPhysicsServer().waitForCreationQueue();

        // Unload resources:
        for(Resource resource : getResourceManager().snapLoadedResources())
            resource.unload();

        // Wait for server destruction queues:
        getVideoServer().waitForDestructionQueue();
        getVideoServer().waitForDestructionQueue();
        getPhysicsServer().waitForDestructionQueue();

        // Stop the servers:
        getVideoServer().stop();
        getAudioServer().stop();
        getPhysicsServer().stop();

        // Destroy the servers:
        getVideoServer().destroy();
        getAudioServer().destroy();
        getPhysicsServer().destroy();

        // Unload libraries:
        glfwTerminate();
    }

    /**
     * Processes system events.<br>
     * • Must be systematically called for the process to keep responding.<br>
     * • Is called from the {@link #run(Consumer)}   main loop}.<br>
     * • Must only be called from the main thread.
     */
    public void process() {
        getVideoServer().getWindow().process();
        glfwPollEvents();

    }

    /**
     * Makes the window visible and enters the main loop, hides the window upon finalization.<br>
     * • Systematically issues calls to {@link #process()}, as required.<br>
     * • Finalized by calling {@link #exit()} from any thread.<br>
     * • Must only be called from the main thread.
     *
     * @param iteration lambda to be called every iteration, set to null for no iteration
     */
    public void run(Consumer<Double> iteration) {
        VideoServer videoServer = getVideoServer();
        LayerManager layerManager = getLayerManager();
        InputManager inputManager = getInputManager();

        videoServer.getWindow().setVisible(true);
        videoServer.getWindow().focus();

        Timer timer = new Timer();
        timer.start();

        shouldExit.set(false);
        while(!shouldExit.get()) {
            inputManager.resetState();
            process();
            inputManager.processEvents();

            Layer[] layers = layerManager.getLayers();

            for(Layer layer : layers) {
                layer.getRoot().inputAll(inputManager.getEvents());
            }

            double delta = timer.getElapsed();
            timer.reset();

            for(Layer layer : layers) {
                layer.getRoot().updateAll(delta);
                layer.genIndex();
            }

            if(iteration != null) iteration.accept(delta);
        }

        videoServer.getWindow().setVisible(false);
    }

    // </editor-fold>

    /**
     * Requests termination of the {@link #run(Consumer)}  main loop}.<br>
     * • May be called from any thread.
     */
    public void exit() {
        shouldExit.set(true);
    }

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
     * Returns the audio server.<br>
     * • May be called from any thread.
     *
     * @return server
     */
    public AudioServer getAudioServer() {
        return audioServer.get();
    }

    /**
     * Returns the physics server.<br>
     * • May be called from any thread.
     *
     * @return server
     */
    public PhysicsServer getPhysicsServer() {
        return physicsServer.get();
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

    /**
     * Returns the resource manager.<br>
     * • May be called from any thread.
     *
     * @return resource manager
     */
    public ResourceManager getResourceManager() {
        return resourceManager.get();
    }

    /**
     * Returns the layer manager.<br>
     * • May be called from any thread.
     *
     * @return layer manager
     */
    public LayerManager getLayerManager() {
        return layerManager.get();
    }

    /**
     * Returns the profiler.<br>
     * • May be called from any thread.
     *
     * @return profiler
     */
    public Profiler getProfiler() {
        return profiler.get();
    }

    /**
     * Returns the input manager.<br>
     * • May be called from any thread.
     *
     * @return input manager
     */
    public InputManager getInputManager() {
        return inputManager.get();
    }

    // </editor-fold>

    private static final Engine INSTANCE = new Engine();

    private final AtomicBoolean shouldExit = new AtomicBoolean();

    // Servers
    private final AtomicReference<VideoServer> videoServer = new AtomicReference<>(null);
    private final AtomicReference<AudioServer> audioServer = new AtomicReference<>(null);
    private final AtomicReference<PhysicsServer> physicsServer = new AtomicReference<>(null);

    // Thread Pools
    private final AtomicReference<ThreadPoolExecutor> loaderPool = new AtomicReference<>(null);
    private final AtomicReference<ThreadPoolExecutor> jobPool = new AtomicReference<>(null);

    // Managers
    private final AtomicReference<ResourceManager> resourceManager = new AtomicReference<>(null);
    private final AtomicReference<LayerManager> layerManager = new AtomicReference<>(null);
    private final AtomicReference<Profiler> profiler = new AtomicReference<>(null);
    private final AtomicReference<InputManager> inputManager = new AtomicReference<>(null);
}
