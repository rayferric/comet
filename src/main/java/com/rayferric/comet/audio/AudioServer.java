package com.rayferric.comet.audio;

import com.rayferric.comet.audio.al.ALAudioEngine;
import com.rayferric.comet.audio.recipe.AudioRecipe;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

public class AudioServer extends Server {
    public AudioServer() {

    }

    @Override
    public void destroy() {

    }

    /**
     * Waits for the audio engine to initialize.<br>
     * • Returns when the audio engine starts processing.<br>
     * • The server must be running.<br>
     * • May be called from any thread.
     *
     * @throws IllegalStateException if the server is stopped
     */
    public void awaitInitialization() {
        synchronized(startStopLock) {
            if(!isRunning())
                throw new IllegalStateException("Attempted to wait for audio engine while the server was down.");
            synchronized(initializedNotifier) {
                try {
                    initializedNotifier.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

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
