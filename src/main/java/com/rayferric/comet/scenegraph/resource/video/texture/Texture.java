package com.rayferric.comet.scenegraph.resource.video.texture;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

import java.nio.ByteBuffer;

public abstract class Texture extends VideoResource {
    public static class ServerRecipe extends Resource.ServerRecipe {
        public ServerRecipe(Runnable cleanUpCallback, Vector2i size, TextureFormat format, TextureFilter filter, ByteBuffer data) {
            super(cleanUpCallback);

            this.size = size;
            this.format = format;
            this.filter = filter;
            this.data = data;
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

        public ByteBuffer getData() {
            return data;
        }

        private final Vector2i size;
        private final TextureFormat format;
        private final TextureFilter filter;
        private final ByteBuffer data;
    }

    @Override
    public void unload() {
        super.unload();
        Engine.getInstance().getVideoServer().scheduleResourceDestruction(handle);
    }

    public long getHandle() {
        return handle;
    }

    protected long handle;
}
