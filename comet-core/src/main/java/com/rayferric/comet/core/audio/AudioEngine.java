package com.rayferric.comet.core.audio;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.scenegraph.resource.audio.AudioSource;
import com.rayferric.comet.core.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.core.server.ServerResource;

import java.nio.ShortBuffer;

public abstract class AudioEngine {
    public void destroy() {
        onStop();
    }

    public void process() {
        onProcess();
    }

    // <editor-fold desc="Internal API">

    public abstract ServerResource createAudioStream(ShortBuffer data, int channels, int sampleRate);

    public abstract ServerResource createAudioSource();

    // </editor-fold>

    protected AudioEngine() {
        onStart();
    }

    // <editor-fold desc="Events">

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onProcess();

    // </editor-fold>

    // <editor-fold desc="Creating and Querying Default Resources"

    protected ServerResource getServerAudioStreamOrNull(AudioStream stream) {
        if(stream == null) return null;
        long handle = stream.getServerHandle();
        return Engine.getInstance().getAudioServer().getServerResource(handle);
    }

    protected ServerResource getServerAudioSourceOrNull(AudioSource source) {
        if(source == null) return null;
        long handle = source.getServerHandle();
        return Engine.getInstance().getAudioServer().getServerResource(handle);
    }

    // </editor-fold>
}
