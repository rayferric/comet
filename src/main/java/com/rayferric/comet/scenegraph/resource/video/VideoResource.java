package com.rayferric.comet.scenegraph.resource.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.atomic.AtomicLong;

public abstract class VideoResource extends Resource {
    @Override
    public boolean unload() {
        if(!super.unload()) return false;
        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle.get());
        return true;
    }

    public long getServerHandle() {
        return serverHandle.get();
    }

    public boolean isServerResourceReady() {
        return Engine.getInstance().getVideoServer().getServerResource(getServerHandle()) != null;
    }

    protected final AtomicLong serverHandle = new AtomicLong();
}
