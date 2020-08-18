package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

import java.util.concurrent.Semaphore;

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

    // Does not implement engine to avoid circular reference
    @Override
    public String toString() {
        return String.format("Resource{properties=%s}", properties);
    }

    public abstract void free();

    public void reload() {
        free();
        create();
    }

    protected interface Properties {}

    protected final Engine engine;
    protected Properties properties;

    protected Resource(Engine engine) {
        this.engine = engine;
    }

    protected abstract void create();
}
