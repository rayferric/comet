package com.rayferric.comet.core.video.recipe.texture;

import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.video.recipe.VideoRecipe;
import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.video.VideoEngine;
import com.rayferric.comet.core.video.util.texture.TextureFormat;

import java.nio.Buffer;

public class Texture2DRecipe extends VideoRecipe {
    public Texture2DRecipe(Runnable cleanUpCallback, Buffer data, Vector2i size, TextureFormat format, boolean filter) {
        super(cleanUpCallback);

        this.data = data;
        this.size = size;
        this.format = format;
        this.filter = filter;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createTexture2D(data, size, format, filter);
    }

    private final Buffer data;
    private final Vector2i size;
    private final TextureFormat format;
    private final boolean filter;
}
