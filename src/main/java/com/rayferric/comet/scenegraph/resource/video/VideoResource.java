package com.rayferric.comet.scenegraph.resource.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.atomic.AtomicLong;

public abstract class VideoResource extends Resource {
    @Override
    public void unload() {
        super.unload();

        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle.get());
    }

    public long getServerHandle() {
        if(!isLoaded())
            throw new RuntimeException("Requested server handle of an unloaded resource.");
        return serverHandle.get();
    }

    protected final AtomicLong serverHandle = new AtomicLong();
}
