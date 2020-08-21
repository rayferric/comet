package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;
import com.rayferric.comet.util.ResourceLoader;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class ImageTexture extends Texture {
    public ImageTexture(boolean fromJar, String path, TextureFilter filter) {
        properties = new Properties();
        properties.fromJar = fromJar;
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
                    ByteBuffer srcData =
                            ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.path);
                    ByteBuffer data = stbi_load_from_memory(srcData, widthBuf, heightBuf, channelsBuf, 0);
                    MemoryUtil.memFree(srcData);
                    if(data == null)
                        throw new RuntimeException("Failed to read image file.\n" + properties.path);

                    Vector2i size = new Vector2i(widthBuf.get(0), heightBuf.get(0));
                    int channels = channelsBuf.get(0);
                    TextureFormat format = switch(channels) {
                        case 1 -> TextureFormat.R8;
                        case 2 -> TextureFormat.RG8;
                        case 3 -> TextureFormat.RGB8;
                        case 4 -> TextureFormat.RGBA8;
                        default -> throw new RuntimeException("Texture has more than 4 channels.");
                    };

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
        public boolean fromJar;
        public String path;
        public TextureFilter filter;
    }

    private final Properties properties;
}
