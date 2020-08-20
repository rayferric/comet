package com.rayferric.comet.video.api;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.ServerRecipe;
import com.rayferric.comet.server.recipe.video.ShaderRecipe;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;

public abstract class VideoEngine {
    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onDraw();

    public abstract void onResize(Vector2i size);

    public abstract ServerResource createTexture2D(Texture2DRecipe recipe);

    public abstract ServerResource createShader(ShaderRecipe recipe);

    public ServerResource resourceFromRecipe(ServerRecipe recipe) {
        if(recipe instanceof Texture2DRecipe)
            return createTexture2D((Texture2DRecipe)recipe);
        else if(recipe instanceof ShaderRecipe)
            return createShader((ShaderRecipe)recipe);
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

    public void createDefaultResources() {
        defaultTexture2D = createDefaultTexture2D();
        defaultShader = createDefaultShader();
    }

    protected Vector2i size;

    protected VideoEngine(Vector2i size) {
        this.size = size;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected VideoEngine(VideoEngine other) {
        this.size = other.size;
    }

    protected abstract ServerResource createDefaultTexture2D();

    protected abstract ServerResource createDefaultShader();

    private ServerResource getTexture2DOrDefault(long handle) {
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        return resource == null ? defaultTexture2D : resource;
    }

    private ServerResource getShaderOrDefault(long handle) {
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        return resource == null ? defaultShader : resource;
    }

    private ServerResource defaultTexture2D, defaultShader;
}
