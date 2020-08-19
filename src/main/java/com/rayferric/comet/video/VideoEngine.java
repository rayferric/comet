package com.rayferric.comet.video;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.server.ServerResource;

public abstract class VideoEngine {
    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onDraw();

    public abstract void onResize(Vector2i size);

    public abstract ServerResource createTexture(Texture.ServerRecipe recipe);

    public abstract ServerResource createShader(Shader.ServerRecipe recipe);

    public ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        if(recipe instanceof Texture.ServerRecipe)
            return createTexture((Texture.ServerRecipe)recipe);
        else if(recipe instanceof Shader.ServerRecipe)
            return createShader((Shader.ServerRecipe)recipe);
        else
            throw new IllegalArgumentException("Attempted to create a video resource of unknown type.");
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
        if(!size.equals(this.size))
            onResize(this.size = size);
    }

    protected Vector2i size;

    protected VideoEngine(Vector2i size) {
        this.size = size;
    }

    protected VideoEngine(VideoEngine other) {
        this.size = other.size;
    }
}
