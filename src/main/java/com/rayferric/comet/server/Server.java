package com.rayferric.comet.server;

import com.rayferric.comet.audio.recipe.AudioSourceRecipe;
import com.rayferric.comet.audio.recipe.AudioStreamRecipe;
import com.rayferric.comet.engine.Engine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO Finish Javadoc
public abstract class Server {
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

            // Must be set from this thread:
            try {
                initializationSemaphore.acquire(1);
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            shuttingDown.set(false);

            thread = new Thread(() -> {
                try {
                    onStart();
                    initializationSemaphore.release(1);

                    while(!shuttingDown.get()) {
                        createNextPendingResource();
                        destroyNextPendingResource();

                        onLoop();
                    }
                    destroyAllResources();

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
     * • The server thread will destroy all resources upon returning.<br>
     * • May be called from any thread.
     */
    public void stop() {
        synchronized(startStopLock) {
            if(!running.get())
                throw new IllegalStateException("Attempted to stop an already stopped server.");

            shuttingDown.set(true);
            try {
                thread.join();
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }

            running.set(false);
        }
    }

    /**
     * Waits for the server to start.<br>
     * • Returns when the server starts processing or immediately if it's down.<br>
     * • May be called from any thread.
     */
    public void awaitInitialization() {
        try {
            initializationSemaphore.acquire(1);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        initializationSemaphore.release(1);
    }

    public long scheduleResourceCreation(ServerRecipe recipe) {
        long handle = handleGenerator.getAndIncrement();
        synchronized(creationMap) {
            creationMap.put(handle, recipe);
        }
        return handle;
    }

    public void scheduleResourceDestruction(long handle) {
        synchronized(destructionList) {
            destructionList.add(handle);
        }
    }

    public ServerResource getServerResource(long handle) {
        synchronized(resources) {
            return resources.get(handle);
        }
    }

    protected abstract void onStart();

    protected abstract void onLoop();

    protected abstract void onStop();

    protected abstract ServerResource resourceFromRecipe(ServerRecipe recipe);

    private Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private final Object startStopLock = new Object();
    private final Semaphore initializationSemaphore = new Semaphore(1);

    private final HashMap<Long, ServerResource> resources = new HashMap<>();
    private final AtomicLong handleGenerator = new AtomicLong(0);
    private final TreeMap<Long, ServerRecipe> creationMap = new TreeMap<>();
    private final List<Long> destructionList = new ArrayList<>();

    // <editor-fold desc="Server Thread Helpers">

    private void createNextPendingResource() {
        Map.Entry<Long, ServerRecipe> entry;
        synchronized(creationMap) {
            entry = creationMap.pollFirstEntry();
        }
        if(entry == null) return;

        long handle = entry.getKey();
        ServerRecipe recipe = entry.getValue();

        ServerResource serverResource = resourceFromRecipe(recipe);
        recipe.cleanUp();

        synchronized(resources) {
            resources.put(handle, serverResource);
        }
    }

    // Returns whether all resources are destroyed
    private void destroyNextPendingResource() {
        int listSize = destructionList.size();
        if(listSize == 0) return;

        long handle = destructionList.remove(destructionList.size() - 1);
        ServerResource serverResource;
        synchronized(resources) {
            serverResource = resources.remove(handle);
        }

        if(serverResource != null)
            serverResource.destroy();
        else {
            ServerRecipe recipe;
            synchronized(creationMap) {
                recipe = creationMap.remove(handle);
            }
            if(recipe != null) recipe.cleanUp();
        }
    }

    private void destroyAllResources() {
        synchronized(resources) {
            resources.forEach((handle, resource) -> resource.destroy());
            resources.clear();
        }
    }

    // </editor-fold>
}
