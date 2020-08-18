package com.rayferric.comet.server;

import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server {
    public Server() {
        this.thread = new Thread(() -> {
            createQueue.clear();
            destroyQueue.clear();

            process();

            while(createQueue.size() > 0)
                createNextPendingServerResource();
            unloadResources();
            while(destroyQueue.size() > 0)
                destroyNextPendingServerResource();
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

    public void reloadResources() {
        resources.forEach((resource, serverResource) -> {
            if(resource.isReady()) resource.reload();
        });
    }

    public void unloadResources() {
        resources.forEach((resource, serverResource) -> {
            if(resource.isReady()) resource.unload();
        });
    }

    // </editor-fold>

    // <editor-fold desc="Public, server-thread-only API">

    // Should only be used internally by the server thread to access corresponding server resources
    public ServerResource getServerResource(Resource resource) {
        if(resource == null || !resource.isReady())
            return null;
        return resources.get(resource);
    }

    // </editor-fold>

    // <editor-fold desc="Base resource API">

    // Only to be used by the base resource to schedule server-side creation
    public void waitForServerResourceCreation(Resource.ServerRecipe recipe) {
        // If the server is stopped before all waiting threads are done, the process will hang
        recipe.getSemaphore().acquireUninterruptibly();
        createQueue.add(recipe);
        recipe.getSemaphore().acquireUninterruptibly();
        recipe.getSemaphore().release();
    }

    // Only to be used by the base resource to schedule server-side clean-up
    public void scheduleServerResourceDestruction(Resource resource) {
        ServerResource serverResource = resources.remove(resource);
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

    protected void createNextPendingServerResource() {
        Resource.ServerRecipe recipe = createQueue.poll();
        if(recipe == null) return;

        ServerResource serverResource = resourceFromRecipe(recipe);

        resources.put(recipe.getResource(), serverResource);

        recipe.getSemaphore().release();
    }

    protected void destroyNextPendingServerResource() {
        ServerResource serverResource = destroyQueue.poll();
        if(serverResource == null) return;

        serverResource.destroy();
    }

    // </editor-fold>

    private final ConcurrentHashMap<Resource, ServerResource> resources = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Resource.ServerRecipe> createQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> destroyQueue = new ConcurrentLinkedQueue<>();
}
