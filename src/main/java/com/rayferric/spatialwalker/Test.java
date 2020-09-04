package com.rayferric.spatialwalker;

import com.rayferric.comet.util.ResourceLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.AL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALC;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.stb.STBVorbis.*;

public class Test {
    public static void main(String[] args) {
        long device = alcOpenDevice((ByteBuffer)null);
        if(device == NULL)
            throw new RuntimeException("Failed to open audio device.");
        long context = alcCreateContext(device, (IntBuffer)null);
        if(!alcMakeContextCurrent(context))
            throw new RuntimeException("Failed to initialize OpenAL context.");

        AL.createCapabilities(ALC.createCapabilities(device));

        int[] sources = new int[10];
        for(int i = 0; i < 10; i++)
        sources[i] = alGenSources();

        int channels, sampleRate;
        ShortBuffer data;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channelsBuf = stack.mallocInt(1);
            IntBuffer sampleRateBuf = stack.mallocInt(1);
            PointerBuffer outputBuf = stack.mallocPointer(1);
            ByteBuffer vorbisFile = ResourceLoader.readBinaryFileToNativeBuffer(false, "pistol.ogg");
            int samples = stb_vorbis_decode_memory(vorbisFile, channelsBuf, sampleRateBuf, outputBuf);
            MemoryUtil.memFree(vorbisFile);
            channels = channelsBuf.get(0);
            sampleRate = sampleRateBuf.get(0);
            data = outputBuf.getShortBuffer(samples * channels);
        }

        int buffer = alGenBuffers();
        alBufferData(buffer, channels == 2 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data, sampleRate);
        MemoryUtil.memFree(data);



        for(int i = 0; i < 10; i++) {
            alSourcei(sources[i], AL_BUFFER, buffer);
            alSourcePlay(sources[i]);
        }

        int state;
        do {
            state = alGetSourcei(sources[0], AL_SOURCE_STATE);
        } while(state == AL_PLAYING);

        for(int i = 0; i < 10; i++)
        alDeleteSources(sources[i]);
        alDeleteBuffers(buffer);
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
