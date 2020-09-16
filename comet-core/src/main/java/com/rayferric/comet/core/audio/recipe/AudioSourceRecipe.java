package com.rayferric.comet.core.audio.recipe;

import com.rayferric.comet.core.audio.AudioEngine;
import com.rayferric.comet.core.server.ServerResource;

public class AudioSourceRecipe extends AudioRecipe {
    public AudioSourceRecipe() {
        super(null);
    }

    @Override
    public ServerResource resolve(AudioEngine audioEngine) {
        return audioEngine.createAudioSource();
    }
}
