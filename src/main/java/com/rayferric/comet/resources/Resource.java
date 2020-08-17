package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

import java.util.concurrent.Semaphore;

public abstract class Resource {
    public static abstract class InternalRecipe {
        public Resource resource;
        public final Semaphore semaphore = new Semaphore(1);

        public InternalRecipe(Resource resource) {
            this.resource = resource;
        }
    }

    public abstract void free();

    public void reload() {
        free();
        create();
    }

    protected static class Properties {}

    protected final Engine engine;
    protected Properties properties;

    protected Resource(Engine engine) {
        this.engine = engine;
    }

    protected abstract void create();
}
