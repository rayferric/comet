package com.rayferric.comet.audio;

import com.rayferric.comet.server.ServerResource;

import static org.lwjgl.openal.AL10.*;

public class ALSource implements ServerResource {
    public ALSource() {
        handle = alGenSources();
    }

    @Override
    public void destroy() {
        alDeleteSources(handle);
    }

    public void play() {
        alSourcePlay(handle);
    }

    public void pause() {
        alSourcePause(handle);
        alSourceStop();
    }

    public void resume() {
        alSourceRewind(handle);
    }

    public int getHandle() {
        return handle;
    }

    private final int handle;
}
