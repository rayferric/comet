package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.recipe.texture.Texture2DRecipe;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.comet.video.util.texture.TextureFormat;

public class EmptyTexture extends Texture {
    public EmptyTexture(Vector2i size, TextureFormat format, boolean filter) {
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
                new Texture2DRecipe(null, null, properties.size, properties.format, properties.filter);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

        finishLoading();
    }

    private static class Properties {
        public Vector2i size;
        public TextureFormat format;
        public boolean filter;
    }

    private final Properties properties;
}
