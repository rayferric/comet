package com.rayferric.comet.scenegraph.resource.video.mesh;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Mesh extends VideoResource {
    @Override
    public void unload() {
        super.unload();

        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle.get());
    }

    public long getServerHandle() {
        return serverHandle.get();
    }

    protected final AtomicLong serverHandle = new AtomicLong();
}
