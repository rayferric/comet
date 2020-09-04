package com.rayferric.comet.audio;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.scenegraph.node.AudioPlayer;
import com.rayferric.comet.scenegraph.resource.audio.AudioSource;
import com.rayferric.comet.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.server.ServerResource;

import java.nio.ShortBuffer;

public abstract class AudioEngine {
    public void destroy() {
        onStop();
    }

    public void process() {
        onProcess();
    }

    // </editor-fold>

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
        if(stream == null || !stream.isLoaded()) return null;
        long handle = stream.getServerHandle();
        return Engine.getInstance().getAudioServer().getServerResource(handle);
    }

    protected ServerResource getServerAudioSourceOrNull(AudioSource source) {
        if(source == null || !source.isLoaded()) return null;
        long handle = source.getServerHandle();
        return Engine.getInstance().getAudioServer().getServerResource(handle);
    }

    // </editor-fold>
}
