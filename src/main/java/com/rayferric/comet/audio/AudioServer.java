package com.rayferric.comet.audio;

import com.rayferric.comet.audio.al.ALAudioEngine;
import com.rayferric.comet.audio.recipe.AudioRecipe;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

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
