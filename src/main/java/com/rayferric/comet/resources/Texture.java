package com.rayferric.comet.resources;

import com.rayferric.comet.Engine;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture extends VideoResource {
    public static class InternalRecipe extends Resource.InternalRecipe {
        public int width, height, channels;
        public ByteBuffer data;

        public InternalRecipe(Resource resource) {
            super(resource);
        }
    }

    public Texture(Engine engine, String path) {
        super(engine);
        properties = new Properties();
        ((Properties)properties).path = path;
        create();
    }

    protected static class Properties extends Resource.Properties {
        public String path;
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
                recipe.data = STBImage.stbi_load(properties.path, width, height, channels, 0);
                if(recipe.data == null)
                    throw new RuntimeException("Failed to read texture file.");
                recipe.width = width.get(0);
                recipe.height = height.get(0);
                recipe.channels = channels.get(0);
            }
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Loaded texture to memory.");

            // This method waits till render thread picks the recipe up
            // Waiting assures that recipe queue on the render thread is not crowded
            // Which makes the overall FPS more stable
            engine.getVideoEngine().waitForResource(recipe);

            System.out.println("Freeing the memory...");
            STBImage.stbi_image_free(recipe.data);
        });
    }
}
