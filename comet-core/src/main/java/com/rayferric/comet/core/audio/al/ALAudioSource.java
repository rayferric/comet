package com.rayferric.comet.core.audio.al;

import com.rayferric.comet.core.math.Mathf;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

public class ALAudioSource implements ServerResource {
    public ALAudioSource() {
        handle = alGenSources();
    }

    @Override
    public void destroy() {
        alDeleteSources(handle);
    }

    public void updatePos(Vector3f pos, double delta) {
        Vector3f prevPos;
        try(MemoryStack stack =MemoryStack.stackPush()) {
            FloatBuffer posBuf = stack.mallocFloat(3);
            alGetSourcefv(handle, AL_POSITION, posBuf);
            prevPos = new Vector3f(posBuf.get(0), posBuf.get(1), posBuf.get(2));
        }
        Vector3f deltaPos = pos.sub(prevPos);
        velocity = velocity.lerp(deltaPos.div((float)delta), (float)Math.sqrt(delta));
        alSource3f(handle, AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());

        alSource3f(handle, AL_POSITION, pos.getX(), pos.getY(), pos.getZ());
    }

    public ALAudioStream getStream() {
        return stream;
    }

    public void setStream(ALAudioStream stream) {
        alSourcei(handle, AL_BUFFER, (this.stream = stream).getHandle());
    }

    public float getGain() {
        return alGetSourcef(handle, AL_GAIN);
    }

    public void setGain(float gain) {
        alSourcef(handle, AL_GAIN, gain);
    }

    public float getPitch() {
        return alGetSourcef(handle, AL_PITCH);
    }

    public void setPitch(float pitch) {
        alSourcef(handle, AL_PITCH, pitch);
    }

    public boolean isLooping() {
        return alGetSourcei(handle, AL_LOOPING) == AL_TRUE;
    }

    public void setLooping(boolean looping) {
        alSourcei(handle, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    public float getAttenuationScale() {
        return alGetSourcef(handle, AL_ROLLOFF_FACTOR);
    }

    public void setAttenuationScale(float scale) {
        alSourcef(handle, AL_ROLLOFF_FACTOR, scale);
    }

    public float getMinDistance() {
        return alGetSourcef(handle, AL_REFERENCE_DISTANCE);
    }

    public void setMinDistance(float distance) {
        alSourcef(handle, AL_REFERENCE_DISTANCE, Mathf.max(distance, 1));
    }

    public boolean isPlaying() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public boolean isPaused() {
        return alGetSourcei(handle, AL_SOURCE_STATE) == AL_PAUSED;
    }

    public void play() {
        alSourcePlay(handle);
    }

    public void pause() {
        alSourcePause(handle);
    }

    public void stop() {
        alSourceStop(handle);
    }

    public void rewind() {
        alSourceRewind(handle);
    }

    public int getHandle() {
        return handle;
    }

    private final int handle;
    private ALAudioStream stream = null;
    private Vector3f velocity = new Vector3f(0);
}
