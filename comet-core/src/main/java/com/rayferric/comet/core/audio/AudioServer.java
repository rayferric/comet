package com.rayferric.comet.core.audio;

import com.rayferric.comet.core.audio.al.ALAudioEngine;
import com.rayferric.comet.core.audio.recipe.AudioRecipe;
import com.rayferric.comet.core.server.Server;
import com.rayferric.comet.core.server.ServerRecipe;
import com.rayferric.comet.core.server.ServerResource;

public class AudioServer extends Server {
    @Override
    public void destroy() {}

    @Override
    protected void onStart() {
        audioEngine = new ALAudioEngine();
    }

    @Override
    protected void onLoop() {
        synchronized(initializedNotifier) {
            initializedNotifier.notifyAll();
        }

        audioEngine.process();
    }

    @Override
    protected void onStop() {
        audioEngine.destroy();
    }

    @Override
    protected ServerResource resourceFromRecipe(ServerRecipe recipe) {
        return ((AudioRecipe)recipe).resolve(audioEngine);
    }

    private AudioEngine audioEngine;
    private final Object initializedNotifier = new Object();
}
