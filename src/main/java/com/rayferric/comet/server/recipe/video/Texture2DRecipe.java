package com.rayferric.comet.server.recipe.video;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.recipe.ServerRecipe;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

import java.nio.ByteBuffer;

public class Texture2DRecipe extends ServerRecipe {
    public Texture2DRecipe(Runnable cleanUpCallback, ByteBuffer data, Vector2i size, TextureFormat format,
                           TextureFilter filter) {
        super(cleanUpCallback);

        this.data = data;
        this.size = size;
        this.format = format;
        this.filter = filter;
    }

    public ByteBuffer getData() {
        return data;
    }

    public Vector2i getSize() {
        return size;
    }

    public TextureFormat getFormat() {
        return format;
    }

    public TextureFilter getFilter() {
        return filter;
    }

    private final ByteBuffer data;
    private final Vector2i size;
    private final TextureFormat format;
    private final TextureFilter filter;
}
