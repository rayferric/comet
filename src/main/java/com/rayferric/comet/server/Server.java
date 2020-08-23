package com.rayferric.comet.server;

import com.rayferric.comet.Engine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Server {
    /**
     * Returns text representation of the current state of the server.<br>
     * • May be called from any thread.
     *
     * @return current state in text format
     */
    @Override
    public String toString() {
        return String
                .format("Server{running=%s, resources=%s, creationQueue=%s, destructionQueue=%s}", running.get(),
                        resources, creationQueue, destructionQueue);
    }

    // <editor-fold desc="Internal API">

    /**
     * Destroys the server making it unable to run again.<br>
     * • Is internally used by {@link Engine#stop()}.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • Must be called from the main thread.
     */
    public abstract void destroy();

    // Used by Engine class and inheriting servers:

    /**
     * Starts the server thread.<br>
     * • Does not alter the current state of server resources. Just starts the processing thread.
     * • May be called from any thread.
     */
    public void start() {
        synchronized(startStopLock) {
            if(!running.compareAndSet(false, true))
                throw new IllegalStateException("Attempted to start an already running server.");

            shouldProcess.set(true);

            thread = new Thread(() -> {
                try {
                    onStart();
                    while(shouldProcess.get()) {
                        if(!resourceCreationPaused) {
                            createNextPendingResource();
                            if(creationQueue.size() == 0)
                                synchronized(creationQueue) {
                                    creationQueue.notifyAll();
                                }
                        }

                        destroyNextPendingResource();
                        if(destructionQueue.size() == 0)
                            synchronized(destructionQueue) {
                                destructionQueue.notifyAll();
                            }

                        onLoop();
                    }
                    onStop();
                } catch(Throwable e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });

            thread.start();
        }
    }

    /**
     * Requests server thread termination and waits for it to shut down.<br>
     * • Does not alter the current state of server resources. Just stops the processing thread.<br>
     * • May be called from any thread.
     */
    public void stop() {
        synchronized(startStopLock) {
            if(!running.get())
                throw new IllegalStateException("Attempted to stop an already stopped server.");

            shouldProcess.set(false);
            try {
                thread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }

            running.set(false);
        }
    }

    // Used by servers and their resources:

    public ServerResource getServerResource(long handle) {
        return resources.get(handle);
    }

    // Used by Resource class descendants:

    public long scheduleResourceCreation(ServerRecipe recipe) {
        long handle = handleGenerator.getAndIncrement();
        recipe.setHandle(handle);
        creationQueue.add(recipe);
        return handle;
    }

    public void scheduleResourceDestruction(long handle) {
        ServerResource serverResource = resources.remove(handle);
        if(serverResource == null)
            throw new IllegalStateException("Attempted to destroy a non-existent server resource.");
        destructionQueue.add(serverResource);
    }

    /**
     * Waits for all enqueued server resources to be created.<br>
     * • The server must be running or the thread will hang.<br>
     * • Is internally used by {@link Engine#stop()}.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     */
    public void waitForCreationQueue() {
        synchronized(creationQueue) {
            try {
                creationQueue.wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Waits for all enqueued server resources to be destroyed.<br>
     * • The server must be running or the thread will hang.<br>
     * • Is internally used by {@link Engine#stop()}.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     */
    public void waitForDestructionQueue() {
        synchronized(destructionQueue) {
            try {
                destructionQueue.wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    // </editor-fold>

    protected boolean resourceCreationPaused = false;

    protected abstract void onStart();

    protected abstract void onLoop();

    protected abstract void onStop();

    protected abstract ServerResource resourceFromRecipe(ServerRecipe recipe);

    private final Object startStopLock = new Object();
    private Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shouldProcess = new AtomicBoolean(false);

    private final ConcurrentHashMap<Long, ServerResource> resources = new ConcurrentHashMap<>();
    private final AtomicLong handleGenerator = new AtomicLong(0);
    private final ConcurrentLinkedQueue<ServerRecipe> creationQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> destructionQueue = new ConcurrentLinkedQueue<>();

    // <editor-fold desc="Helpers">

    private void createNextPendingResource() {
        ServerRecipe recipe = creationQueue.poll();
        if(recipe == null) return;

        ServerResource serverResource = resourceFromRecipe(recipe);

        recipe.getCleanUpCallback().run();
        resources.put(recipe.getHandle(), serverResource);
    }

    private void destroyNextPendingResource() {
        ServerResource serverResource = destructionQueue.poll();
        if(serverResource == null) return;

        serverResource.destroy();
    }

    // </editor-fold>
}
