package com.rayferric.comet.video.common;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.Texture;
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

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onDraw();

    public abstract void onResize(Vector2i size);

    public abstract ServerResource createTexture(Texture.ServerRecipe recipe);

    public ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        if(recipe instanceof Texture.ServerRecipe)
            return createTexture((ImageTexture.ServerRecipe)recipe);
        else
            throw new RuntimeException("Attempted to create a video resource of unknown type.");
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
        if(!size.equals(this.size))
            onResize(this.size = size);
    }

    protected Vector2i size;
}
