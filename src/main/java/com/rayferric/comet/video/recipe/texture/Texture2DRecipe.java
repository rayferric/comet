package com.rayferric.comet.video.recipe.texture;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.comet.video.util.texture.TextureFormat;

import java.nio.Buffer;

public class Texture2DRecipe extends VideoRecipe {
    public Texture2DRecipe(Runnable cleanUpCallback, Buffer data, Vector2i size, TextureFormat format,
                           TextureFilter filter) {
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
    private final TextureFilter filter;
}
