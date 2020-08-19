package com.rayferric.comet.server;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Server {
    public Server() {
        this.thread = new Thread(() -> {
            createQueue.clear();
            destroyQueue.clear();

            process();

            while(createQueue.size() > 0)
                createNextPendingResource();
            resources.forEach((handle, resource) -> scheduleResourceDestruction(handle));
            while(destroyQueue.size() > 0)
                destroyNextPendingResource();
        });
    }

    @Override
    public String toString() {
        return String
                .format("Server{thread=%s, running=%s, resources=%s, createQueue=%s, destroyQueue=%s}", thread, running,
                        resources, createQueue, destroyQueue);
    }

    // <editor-fold desc="Public API">

    /**
     * Starts the server thread.
     */
    public void start() {
        running.set(true);
        thread.start();
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
            System.exit(1);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Public, server-thread-only API">

    // Should only be used internally by the server thread to access corresponding server resources
    public ServerResource getServerResource(long handle) {
        return resources.get(handle);
    }

    // </editor-fold>

    // <editor-fold desc="Base resource API">

    public long scheduleResourceCreation(Resource.ServerRecipe recipe) {
        long handle = handleGenerator.getAndIncrement();
        recipe.setHandle(handle);
        createQueue.add(recipe);
        return handle;
    }

    public void scheduleResourceDestruction(long handle) {
        ServerResource serverResource = resources.remove(handle);
        if(serverResource == null)
            throw new RuntimeException("Attempted to destroy a non-existent server resource.");
        destroyQueue.add(serverResource);
    }

    // </editor-fold>

    protected Thread thread;
    protected final AtomicBoolean running = new AtomicBoolean(false);

    protected abstract void process();

    protected abstract ServerResource resourceFromRecipe(Resource.ServerRecipe recipe);

    // <editor-fold desc="Internal helpers for inherited classes">

    // Use those to incrementally process pending resources

    protected void createNextPendingResource() {
        Resource.ServerRecipe recipe = createQueue.poll();
        if(recipe == null) return;

        ServerResource serverResource = resourceFromRecipe(recipe);

        resources.put(recipe.getHandle(), serverResource);
        Engine.getInstance().getLoaderPool().execute(recipe.getCleanUpCallback());
    }

    protected void destroyNextPendingResource() {
        ServerResource serverResource = destroyQueue.poll();
        if(serverResource == null) return;

        serverResource.destroy();
    }

    // </editor-fold>

    private final ConcurrentHashMap<Long, ServerResource> resources = new ConcurrentHashMap<>();
    private final AtomicLong handleGenerator = new AtomicLong(0);
    private final ConcurrentLinkedQueue<Resource.ServerRecipe> createQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> destroyQueue = new ConcurrentLinkedQueue<>();
}
