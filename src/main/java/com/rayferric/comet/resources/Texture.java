package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture extends Resource {
    public Texture(Engine engine, String path) {
        super(engine);
        properties = new Properties(path);
        load();
    }

    protected static class Properties extends Resource.Properties {
        public String path;

        public Properties(String path) {
            this.path = path;
        }
    }

    protected ResourceHandle videoResource;

    @Override
    protected void load() {
        Properties properties = (Properties)this.properties;

        engine.getThreadPool().execute(() -> {
            ByteBuffer data;
            STBImage.stbi_set_flip_vertically_on_load(true);
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                data = STBImage.stbi_load(properties.path, width, height, channels, 0);
            }
            videoResource =
        });
    }

    @Override
    protected void unload() {

    }
}
