package com.rayferric.comet.scenegraph.resource.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ImageTexture extends Texture {
    public ImageTexture(String path, TextureFilter filter) {
        properties = new Properties(path, filter);
        load();
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuf = stack.mallocInt(1);
                IntBuffer heightBuf = stack.mallocInt(1);
                IntBuffer channelsBuf = stack.mallocInt(1);
                STBImage.stbi_set_flip_vertically_on_load(true);
                ByteBuffer data = STBImage.stbi_load(properties.getPath(), widthBuf, heightBuf, channelsBuf, 0);
                if(data == null)
                    throw new RuntimeException("Failed to read texture file.");
                Vector2i size = new Vector2i(widthBuf.get(0), heightBuf.get(0));
                int channels = channelsBuf.get(0);
                TextureFormat format;
                switch(channels) {
                    case 1: format = TextureFormat.R8; break;
                    case 2: format = TextureFormat.RG8; break;
                    case 3: format = TextureFormat.RGB8; break;
                    case 4: format = TextureFormat.RGBA8; break;
                    default:
                        throw new RuntimeException("Texture has more than 4 channels.");
                }

                ServerRecipe recipe = new ServerRecipe(() -> {
                    STBImage.stbi_image_free(data);
                    markAsReady();
                }, size, format, properties.filter, data);

                handle = Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe);
            }
        });
    }

    private static class Properties {
        public Properties(String path, TextureFilter filter) {
            this.path = path;
            this.filter = filter;
        }

        public String getPath() {
            return path;
        }

        public TextureFilter getFilter() {
            return filter;
        }

        private final String path;
        private final TextureFilter filter;
    }

    private final Properties properties;
}
