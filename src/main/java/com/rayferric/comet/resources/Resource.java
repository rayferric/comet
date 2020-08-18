package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

import java.util.Objects;
import java.util.concurrent.Semaphore;

public abstract class Resource {
    public static abstract class InternalRecipe {
        public InternalRecipe(Resource resource) {
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
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Resource other = (Resource)o;
        return Objects.equals(engine, other.engine) &&
                Objects.equals(properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engine, properties);
    }

    @Override
    public String toString() {
        return String.format("Resource{engine=%s, properties=%s}", engine, properties);
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
