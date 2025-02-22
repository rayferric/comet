package com.rayferric.comet.core.scenegraph.resource.physics;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.scenegraph.resource.Resource;

import java.util.concurrent.atomic.AtomicLong;

public class PhysicsResource extends Resource {
    @Override
    public boolean unload() {
        if(!super.unload()) return false;
        Engine.getInstance().getPhysicsServer().scheduleResourceDestruction(serverHandle.get());
        return true;
    }

    public long getServerHandle() {
        return serverHandle.get();
    }

    public boolean isServerResourceReady() {
        return Engine.getInstance().getPhysicsServer().getServerResource(getServerHandle()) != null;
    }

    protected final AtomicLong serverHandle = new AtomicLong(-1);
}
