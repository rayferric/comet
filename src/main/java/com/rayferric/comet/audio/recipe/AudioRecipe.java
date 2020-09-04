package com.rayferric.comet.audio.recipe;

import com.rayferric.comet.audio.AudioEngine;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

public abstract class AudioRecipe extends ServerRecipe {
    public AudioRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(AudioEngine audioEngine);
}
