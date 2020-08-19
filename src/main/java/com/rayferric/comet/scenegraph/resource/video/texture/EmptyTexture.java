package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

public class EmptyTexture extends Texture {
    public EmptyTexture(Vector2i size, TextureFormat format, TextureFilter filter) {
        properties = new Properties(size, format, filter);
        load();
    }

    @Override
    public void load() {
        Texture.ServerRecipe recipe = new Texture.ServerRecipe(this::markAsReady, properties.size, properties.format, properties.filter, null);
        handle = Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe);
    }

    private static class Properties {
        public Properties(Vector2i size, TextureFormat format, TextureFilter filter) {
            this.size = size;
            this.format = format;
            this.filter = filter;
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

        private final Vector2i size;
        private final TextureFormat format;
        private final TextureFilter filter;
    }

    private final Properties properties;
}
