package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

public abstract class VideoResource extends Resource {
    @Override
    public void free() {
        engine.getVideoEngine().freeResource(this);
    }

    protected VideoResource(Engine engine) {
        super(engine);
    }
}
