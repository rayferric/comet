package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

public class EmptyTexture extends Texture {
    public EmptyTexture(Vector2i size, TextureFormat format, TextureFilter filter) {
        properties = new Properties();
        properties.size = size;
        properties.format = format;
        properties.filter = filter;

        load();
    }

    @Override
    public void load() {
        super.load();

        Texture2DRecipe recipe =
                new Texture2DRecipe(this::finishLoading, null, properties.size, properties.format,
                        properties.filter);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
    }

    private static class Properties {
        public Vector2i size;
        public TextureFormat format;
        public TextureFilter filter;
    }

    private final Properties properties;
}
