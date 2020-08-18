package com.rayferric.comet.video.common;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.ImageTexture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.VideoServer;

public abstract class VideoEngine {
    public VideoEngine(Vector2i size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    public abstract void start();

    public abstract void stop();

    public abstract void draw();

    public void resize(Vector2i size) {
        this.size = size;
    }

    public ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        if(recipe instanceof ImageTexture.ServerRecipe)
            return createImageTexture((ImageTexture.ServerRecipe)recipe);
        else
            throw new RuntimeException("Attempted to create a video resource of unknown type.");
    }

    public abstract ServerResource createImageTexture(ImageTexture.ServerRecipe recipe);

    public Vector2i getSize() {
        return size;
    }

    protected Vector2i size;
}
