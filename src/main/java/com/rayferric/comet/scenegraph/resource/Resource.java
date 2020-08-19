package com.rayferric.comet.scenegraph.resource;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Resource {
    public static abstract class ServerRecipe {
        public ServerRecipe(Runnable cleanUpCallback) {
            this.cleanUpCallback = cleanUpCallback;
        }

        public Runnable getCleanUpCallback() {
            return cleanUpCallback;
        }

        public long getHandle() {
            return handle;
        }

        public void setHandle(long handle) {
            this.handle = handle;
        }

        private final Runnable cleanUpCallback;
        private long handle = 0;
    }

    public boolean isReady() {
        return loaded.get();
    }

    public void load() {
        if(loaded.get())
            throw new RuntimeException("Cannot load an already loaded resource.");
    }

    public void unload() {
        if(!loaded.compareAndSet(true, false))
            throw new RuntimeException("Cannot unload an already unloaded resource.");
    }

    public void reload() {
        if(loaded.get()) unload();
        load();
    }

    protected final AtomicBoolean loaded = new AtomicBoolean(false);

    protected void markAsReady() {
        if(!loaded.compareAndSet(false, true))
            throw new RuntimeException("Attempted to load a single resource using multiple threads simultaneously.");
    }
}
