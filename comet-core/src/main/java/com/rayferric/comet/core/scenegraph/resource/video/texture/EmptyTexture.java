package com.rayferric.comet.core.scenegraph.resource.video.texture;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.video.recipe.texture.Texture2DRecipe;
import com.rayferric.comet.core.video.util.texture.TextureFormat;

public class EmptyTexture extends Texture {
    public EmptyTexture(Vector2i size, TextureFormat format, boolean filter) {
        properties = new Properties();
        properties.size = size;
        properties.format = format;
        properties.filter = filter;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Texture2DRecipe recipe =
                new Texture2DRecipe(null, null, properties.size, properties.format, properties.filter);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
        finishLoading();

        return true;
    }

    private static class Properties {
        public Vector2i size;
        public TextureFormat format;
        public boolean filter;
    }

    private final Properties properties;
}
