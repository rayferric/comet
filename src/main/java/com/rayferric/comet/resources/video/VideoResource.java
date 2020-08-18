package com.rayferric.comet.resources.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.resources.Resource;

public abstract class VideoResource extends Resource {
    @Override
    public void free() {
        engine.getVideoServer().freeResource(this);
    }

    protected VideoResource(Engine engine) {
        super(engine);
    }
}
