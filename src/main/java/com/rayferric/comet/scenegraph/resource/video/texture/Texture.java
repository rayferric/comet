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
        public ServerRecipe(Runnable cleanUpCallback, ByteBuffer data, Vector2i size, TextureFormat format, TextureFilter filter) {
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

    @Override
    public void unload() {
        super.unload();
        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle);
    }

    public long getServerHandle() {
        return serverHandle;
    }

    protected long serverHandle;
}
