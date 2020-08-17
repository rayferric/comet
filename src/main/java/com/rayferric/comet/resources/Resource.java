package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

public class Resource {
    public void reload() {
        unload();
        load();
    }

    public void free() {
        unload();
    }

    protected static class Properties {};

    protected final Engine engine;
    protected Properties properties;

    protected Resource(Engine engine) {
        this.engine = engine;
        engine.registerResource(this);
    }

    protected void load() {

    }

    protected void unload() {

    }
}
