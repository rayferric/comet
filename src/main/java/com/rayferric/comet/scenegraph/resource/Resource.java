package com.rayferric.comet.scenegraph.resource;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Resource {
    public static abstract class ServerRecipe {
        public ServerRecipe(Resource resource) {
            this.resource = resource;
        }

        public Resource getResource() {
            return resource;
        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        private final Resource resource;
        private final Semaphore semaphore = new Semaphore(1);
    }

    @Override
    public String toString() {
        return String.format("Resource{properties=%s}", properties);
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

    protected interface Properties {}

    protected Properties properties;
    protected final AtomicBoolean loaded = new AtomicBoolean(false);

    protected void markAsReady() {
        if(!loaded.compareAndSet(false, true))
            throw new RuntimeException("Attempted to load a single resource using multiple threads.");
    }
}
