package com.rayferric.comet.audio.al;

import com.rayferric.comet.audio.AudioEngine;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.AudioPlayer;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.server.ServerResource;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.rayferric.comet.util.Timer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ALAudioEngine extends AudioEngine {
    @Override
    public ServerResource createAudioStream(ShortBuffer data, int channels, int sampleRate) {
        return new ALAudioStream(data, channels, sampleRate);
    }

    @Override
    public ServerResource createAudioSource() {
        return new ALAudioSource();
    }

    @Override
    protected void onStart() {
        device = alcOpenDevice((ByteBuffer)null);
        if(device == NULL)
            throw new RuntimeException("Failed to open audio device.");
        context = alcCreateContext(device, (IntBuffer)null);
        if(!alcMakeContextCurrent(context))
            throw new RuntimeException("Failed to initialize OpenAL context.");
        AL.createCapabilities(ALC.createCapabilities(device));

        deltaTimer = new Timer();

        deltaTimer.start();

        System.out.println("OpenAL audio engine started.");
    }

    @Override
    protected void onStop() {
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);

        System.out.println("OpenAL audio engine stopped.");
    }

    @Override
    protected void onProcess() {
        double deltaTime = deltaTimer.getElapsed();
        deltaTimer.reset();

        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            Camera camera = layer.getCamera();
            if(camera == null) continue;
            Matrix4f viewMatrix = camera.getGlobalTransform().inverse();

            for(AudioPlayer player : layer.getIndex().getAudioSources()) {
                ALAudioSource alSource = (ALAudioSource)getServerAudioSourceOrNull(player.getSource());
                if(alSource == null) continue;
                ALAudioStream alStream = (ALAudioStream)getServerAudioStreamOrNull(player.getStream());
                if(alStream == null) continue;

                // Update Source

                Vector3f pos = viewMatrix.mul(player.getGlobalTransform()).mul(new Vector3f(0, 0, -1), 1);
                alSource.updatePos(pos, deltaTime);

                if(!alSource.isPlaying() && alSource.getStream() != alStream) alSource.setStream(alStream);

                // Playback Control

                if(!alSource.isPlaying() && player.popPlayCounter()) alSource.play();

                if(player.popShouldReset() && alSource.isPlaying()) alSource.stop();

                if(player.isPaused()) {
                    if(!alSource.isPaused()) alSource.pause();
                } else if(alSource.isPaused()) alSource.play();

                // Set Properties

                alSource.setLooping(player.isLooping());
                alSource.setGain(player.getGain());
                alSource.setPitch(player.getPitch());
                alSource.setAttenuationScale(player.getAttenuationScale());
                alSource.setMinDistance(player.getMinDistance());

                // Update Player State

                player.setPlaying(alSource.isPlaying());
            }
        }

        try {
            Thread.sleep(1);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private long device, context;
    private Timer deltaTimer;
}
