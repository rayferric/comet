package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

public abstract class Resource {
    public void reload() {
        free();
        create();
    }

    public abstract void free();

    protected static class Properties {};

    protected final Engine engine;
    protected Properties properties;

    protected Resource(Engine engine) {
        this.engine = engine;
        engine.registerResource(this);
    }

    protected abstract void create();
}
