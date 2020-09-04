package com.rayferric.comet.audio.recipe;

import com.rayferric.comet.audio.AudioEngine;
import com.rayferric.comet.server.ServerResource;

import java.nio.ShortBuffer;

public class AudioStreamRecipe extends AudioRecipe {
    public AudioStreamRecipe(Runnable cleanUpCallback, ShortBuffer data, int channels, int sampleRate) {
        super(cleanUpCallback);
        this.data = data;
        this.channels = channels;
        this.sampleRate = sampleRate;
    }

    @Override
    public ServerResource resolve(AudioEngine audioEngine) {
        return audioEngine.createAudioStream(data, channels, sampleRate);
    }

    private final ShortBuffer data;
    private final int channels, sampleRate;
}
