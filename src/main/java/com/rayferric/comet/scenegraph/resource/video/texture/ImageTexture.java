package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;
import com.rayferric.comet.video.common.TextureFilter;
import com.rayferric.comet.video.common.TextureFormat;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class ImageTexture extends Texture {
    public ImageTexture(String path, TextureFilter filter) {
        properties = new Properties();
        properties.path = path;
        properties.filter = filter;

        load();
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                try(MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer widthBuf = stack.mallocInt(1);
                    IntBuffer heightBuf = stack.mallocInt(1);
                    IntBuffer channelsBuf = stack.mallocInt(1);
                    stbi_set_flip_vertically_on_load(true);
                    ByteBuffer data = stbi_load(properties.path, widthBuf, heightBuf, channelsBuf, 0);
                    if(data == null)
                        throw new RuntimeException("Failed to read image file.\n" + properties.path);

                    Vector2i size = new Vector2i(widthBuf.get(0), heightBuf.get(0));
                    int channels = channelsBuf.get(0);
                    TextureFormat format;
                    switch(channels) {
                        case 1:
                            format = TextureFormat.R8;
                            break;
                        case 2:
                            format = TextureFormat.RG8;
                            break;
                        case 3:
                            format = TextureFormat.RGB8;
                            break;
                        case 4:
                            format = TextureFormat.RGBA8;
                            break;
                        default:
                            throw new RuntimeException("Texture has more than 4 channels.");
                    }

                    Texture2DRecipe recipe = new Texture2DRecipe(() -> {
                        stbi_image_free(data);
                        finishLoading();
                    }, data, size, format, properties.filter);

                    serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
                }
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static class Properties {
        public String path;
        public TextureFilter filter;
    }

    private final Properties properties;
}
