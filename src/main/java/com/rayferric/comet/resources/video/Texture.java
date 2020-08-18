package com.rayferric.comet.resources.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.resources.Resource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture extends VideoResource {
    public static class InternalRecipe extends Resource.InternalRecipe {
        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getChannels() {
            return channels;
        }

        public ByteBuffer getData() {
            return data;
        }

        public InternalRecipe(Resource resource) {
            super(resource);
        }

        private int width, height, channels;
        private ByteBuffer data;
    }

    public Texture(Engine engine, String path) {
        super(engine);
        properties = new Properties(path);
        create();
    }

    protected static class Properties implements Resource.Properties {
        public Properties(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        private final String path;
    }

    @Override
    protected void create() {
        Properties properties = (Properties)this.properties;

        engine.getThreadPool().execute(() -> {
            InternalRecipe recipe = new InternalRecipe(this);

            STBImage.stbi_set_flip_vertically_on_load(true);
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                recipe.data = STBImage.stbi_load(properties.getPath(), width, height, channels, 0);
                if(recipe.data == null)
                    throw new RuntimeException("Failed to read texture file.");
                recipe.width = width.get(0);
                recipe.height = height.get(0);
                recipe.channels = channels.get(0);
            }

            // This method waits till render thread picks the recipe up
            // Waiting assures that recipe queue on the render thread is not crowded
            // Which makes the overall FPS more stable
            engine.getVideoServer().waitForResource(recipe);

            STBImage.stbi_image_free(recipe.data);
        });
    }
}
