package com.rayferric.comet.scenegraph.resource.audio;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.concurrent.atomic.AtomicLong;

public class AudioResource extends Resource {
    @Override
    public boolean unload() {
        if(!super.unload()) return false;
        Engine.getInstance().getAudioServer().scheduleResourceDestruction(serverHandle.get());
        return true;
    }

    public long getServerHandle() {
        return serverHandle.get();
    }

    public boolean isServerResourceReady() {
        return Engine.getInstance().getAudioServer().getServerResource(getServerHandle()) != null;
    }

    protected final AtomicLong serverHandle = new AtomicLong();
}
