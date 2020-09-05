package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.scenegraph.resource.audio.AudioSource;
import com.rayferric.comet.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.util.AtomicFloat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AudioPlayer extends Node {
    public AudioPlayer() {
        setName("Audio Source");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    // <editor-fold desc="Internal API">

    /**
     * Internal method used by the audio engine.
     *
     * @return the audio source of this player
     */
    public AudioSource getSource() {
        return source;
    }

    /**
     * Internal method used by the audio engine.
     *
     * @return whether the player was to play a sound
     */
    public boolean popPlayCounter() {
        return playCount.getAndUpdate(x -> x > 0 ? x - 1 : x) > 0;
    }

    /**
     * Internal method used by the audio engine.
     *
     * @return whether the playback must be stopped
     */
    public boolean popShouldReset() {
        return shouldReset.compareAndSet(true, false);
    }

    /**
     * Internal method used by the audio engine.
     *
     * @param playing whether the player is currently playing
     */
    public void setPlaying(boolean playing) {
        this.playing.set(playing);
    }

    // </editor-fold>

    public AudioStream getStream() {
        return stream.get();
    }

    public void setStream(AudioStream stream) {
        this.stream.set(stream);
    }

    // <editor-fold desc="Playback Control">

    public void play() {
        playCount.getAndIncrement();
    }

    public void reset() {
        playCount.set(0);
        shouldReset.set(true);
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public boolean isPaused() {
        return paused.get();
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    // </editor-fold>

    public boolean isLooping() {
        return looping.get();
    }

    public void setLooping(boolean looping) {
        this.looping.set(looping);
    }

    public float getGain() {
        return gain.get();
    }

    public void setGain(float gain) {
        this.gain.set(gain);
    }

    public float getPitch() {
        return pitch.get();
    }

    public void setPitch(float pitch) {
        this.pitch.set(pitch);
    }

    public float getAttenuationScale() {
        return attenuationScale.get();
    }

    public void setAttenuationScale(float attenuationScale) {
        this.attenuationScale.set(attenuationScale);
    }

    public float getMinDistance() {
        return minDistance.get();
    }

    public void setMinDistance(float minDistance) {
        this.minDistance.set(minDistance);
    }

    private final AudioSource source = new AudioSource();
    private final AtomicReference<AudioStream> stream = new AtomicReference<>(null);
    private final AtomicInteger playCount = new AtomicInteger(0);
    private final AtomicBoolean shouldReset = new AtomicBoolean(false);
    private final AtomicBoolean playing = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean looping = new AtomicBoolean(false);
    private final AtomicFloat gain = new AtomicFloat(1);
    private final AtomicFloat pitch = new AtomicFloat(1);
    private final AtomicFloat attenuationScale = new AtomicFloat(1);
    private final AtomicFloat minDistance = new AtomicFloat(1);

}
