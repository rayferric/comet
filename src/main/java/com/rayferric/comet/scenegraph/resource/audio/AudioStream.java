package com.rayferric.comet.scenegraph.resource.audio;

import com.rayferric.comet.audio.recipe.AudioStreamRecipe;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.util.ResourceLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

public class AudioStream extends AudioResource {
    public AudioStream(boolean fromJar, String path) {
        properties = new Properties();
        properties.path = path;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                try(MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer channelsBuf = stack.mallocInt(1);
                    IntBuffer sampleRateBuf = stack.mallocInt(1);
                    PointerBuffer outputBuf = stack.mallocPointer(1);

                    ByteBuffer vorbisFile = ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.path);
                    int samples = stb_vorbis_decode_memory(vorbisFile, channelsBuf, sampleRateBuf, outputBuf);
                    MemoryUtil.memFree(vorbisFile);

                    int channels = channelsBuf.get(0);
                    int sampleRate = sampleRateBuf.get(0);
                    ShortBuffer data = outputBuf.getShortBuffer(samples * channels);

                    AudioStreamRecipe recipe = new AudioStreamRecipe(() -> {
                        MemoryUtil.memFree(data);
                    }, data, channels, sampleRate);
                    serverHandle.set(Engine.getInstance().getAudioServer().scheduleResourceCreation(recipe));

                    finishLoading();
                }
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        return true;
    }

    private static class Properties {
        public boolean fromJar;
        public String path;
    }

    private final Properties properties;
}
