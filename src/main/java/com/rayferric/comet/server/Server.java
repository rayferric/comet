package com.rayferric.comet.server;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Server {
    @Override
    public String toString() {
        return String
                .format("Server{thread=%s, running=%s, resources=%s, createQueue=%s, destroyQueue=%s}", thread,
                        shouldProcess,
                        resources, creationQueue, destructionQueue);
    }

    // <editor-fold desc="Public API">

    /**
     * Starts the server thread.
     */
    public void start() {
        if(isRunning())
            throw new RuntimeException("The server must be stopped in order to be able to start it.");

        running.set(true);

        thread = new Thread(() -> {
            shouldProcess.set(true);
            onStart();
            while(shouldProcess.get()) {
                createNextPendingResource();
                if(creationQueue.size() == 0)
                    synchronized(creationQueue) {
                        creationQueue.notifyAll();
                    }

                destroyNextPendingResource();
                if(destructionQueue.size() == 0)
                    synchronized(destructionQueue) {
                        destructionQueue.notifyAll();
                    }

                onLoop();
            }
            onStop();
        });

        thread.start();
    }

    /**
     * Requests server thread termination and waits for it to shut down.<br>
     * Does not create nor destroy pending server resources.
     */
    public void stop() {
        if(!isRunning())
            throw new RuntimeException("The server must be running in order to be able to stop it.");
        shouldProcess.set(false);
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Waits for all enqueued server resources to be created.
     * The server must be running.
     *
     * @throws InterruptedException    if interrupted
     */
    public void waitForCreationQueue() throws InterruptedException {
        synchronized(creationQueue) {
            creationQueue.wait();
        }
    }

    /**
     * Waits for all enqueued server resources to be destroyed.
     * The server must be running.
     *
     * @throws InterruptedException    if interrupted
     */
    public void waitForDestructionQueue() throws InterruptedException {
        synchronized(destructionQueue) {
            destructionQueue.wait();
        }
    }

    // Should only be used internally by the server thread to access corresponding server resources
    public ServerResource getServerResource(long handle) {
        return resources.get(handle);
    }

    // </editor-fold>

    // <editor-fold desc="Base resource API">

    public long scheduleResourceCreation(Resource.ServerRecipe recipe) {
        long handle = handleGenerator.getAndIncrement();
        recipe.setHandle(handle);
        creationQueue.add(recipe);
        return handle;
    }

    public void scheduleResourceDestruction(long handle) {
        ServerResource serverResource = resources.remove(handle);
        if(serverResource == null)
            throw new RuntimeException("Attempted to destroy a non-existent server resource.");
        destructionQueue.add(serverResource);
    }

    // </editor-fold>

    protected Thread thread;
    protected final AtomicBoolean running = new AtomicBoolean(false);
    protected final AtomicBoolean shouldProcess = new AtomicBoolean(false);

    protected abstract void onStart();
    protected abstract void onLoop();
    protected abstract void onStop();

    protected abstract ServerResource resourceFromRecipe(Resource.ServerRecipe recipe);

    // <editor-fold desc="Internal helpers for inherited classes">

    // Use those to incrementally process pending resources

    protected void createNextPendingResource() {
        Resource.ServerRecipe recipe = creationQueue.poll();
        if(recipe == null) return;

        ServerResource serverResource = resourceFromRecipe(recipe);

        resources.put(recipe.getHandle(), serverResource);
        recipe.getCleanUpCallback().run();
    }

    protected void destroyNextPendingResource() {
        ServerResource serverResource = destructionQueue.poll();
        if(serverResource == null) return;

        serverResource.destroy();
    }

    // </editor-fold>

    private final ConcurrentHashMap<Long, ServerResource> resources = new ConcurrentHashMap<>();
    private final AtomicLong handleGenerator = new AtomicLong(0);
    private final ConcurrentLinkedQueue<Resource.ServerRecipe> creationQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> destructionQueue = new ConcurrentLinkedQueue<>();
}
