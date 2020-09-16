package com.rayferric.comet.core.audio.recipe;

import com.rayferric.comet.core.audio.AudioEngine;
import com.rayferric.comet.core.server.ServerRecipe;
import com.rayferric.comet.core.server.ServerResource;

public abstract class AudioRecipe extends ServerRecipe {
    public AudioRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(AudioEngine audioEngine);
}
