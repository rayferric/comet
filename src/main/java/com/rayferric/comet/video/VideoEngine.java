package com.rayferric.comet.video;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.server.ServerResource;

public abstract class VideoEngine {
    public VideoEngine(Vector2i size) {
        this.size = size;
    }

    public abstract void init();

    public abstract void draw();

    public void resize(Vector2i size) {
        this.size = size;
    }

    public abstract ServerResource createTexture(Texture.ServerRecipe recipe);

    public Vector2i getSize() {
        return size;
    }

    protected Vector2i size;
}
