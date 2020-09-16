package com.rayferric.comet.core.audio.al;

import com.rayferric.comet.core.server.ServerResource;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class ALAudioStream implements ServerResource {
    public ALAudioStream(ShortBuffer data, int channels, int sampleRate) {
        handle = alGenBuffers();
        alBufferData(handle, channels == 2 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data, sampleRate);
    }

    @Override
    public void destroy() {
        alDeleteBuffers(handle);
    }

    public int getHandle() {
        return handle;
    }

    private final int handle;
}
