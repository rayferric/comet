package com.rayferric.comet.scenegraph.resource.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

public abstract class VideoResource extends Resource {
    @Override
    public void unload() {
        super.unload();
        Engine.getInstance().getVideoServer().freeServerResource(this);
    }
}
