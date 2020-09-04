package com.rayferric.comet.audio.recipe;

import com.rayferric.comet.audio.AudioEngine;
import com.rayferric.comet.server.ServerResource;

public class AudioSourceRecipe extends AudioRecipe {
    public AudioSourceRecipe() {
        super(null);
    }

    @Override
    public ServerResource resolve(AudioEngine audioEngine) {
        return audioEngine.createAudioSource();
    }
}
