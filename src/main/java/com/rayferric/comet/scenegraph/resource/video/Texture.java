package com.rayferric.comet.scenegraph.resource.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture extends VideoResource {
    public static class ServerRecipe extends Resource.ServerRecipe {
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

        public ServerRecipe(Resource resource) {
            super(resource);
        }

        private int width, height, channels;
        private ByteBuffer data;
    }

    /**
     * Creates a texture resource and begins loading it.
     *
     * @param path location of the source image
     */
    public Texture(String path) {
        properties = new Properties(path);
        load();
    }

    @Override
    public void load() {
        super.load();

        Properties properties = (Properties)this.properties;

        Engine.getInstance().getThreadPool().execute(() -> {
            ServerRecipe recipe = new ServerRecipe(this);

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
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Loaded resource recipe, creating server resource...");
            Engine.getInstance().getVideoServer().waitForServerResource(recipe);

            STBImage.stbi_image_free(recipe.data);

            markAsReady();
        });
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
}
