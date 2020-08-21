package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.ServerRecipe;
import com.rayferric.comet.server.recipe.video.BinaryShaderRecipe;
import com.rayferric.comet.server.recipe.video.MeshRecipe;
import com.rayferric.comet.server.recipe.video.SourceShaderRecipe;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;

/**
 * A cross-API video interface.<br>
 * • Used internally by {@link VideoServer}.<br>
 * • Is not thread safe.
 */
public abstract class VideoEngine {
    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    public void start() {
        onStart();
        defaultTexture2D = createDefaultTexture2D();
        defaultShader = createDefaultShader();
        defaultMesh = createDefaultMesh();
        onResize();
    }

    public void stop() {
        onStop();
    }

    public void draw() {
        onDraw();
    }

    public void resize(Vector2i size) {
        if(!size.equals(this.size)) {
            this.size = new Vector2i(size);
            onResize();
        }
    }

    public ServerResource resourceFromRecipe(ServerRecipe recipe) {
        if(recipe instanceof Texture2DRecipe)
            return createTexture2D((Texture2DRecipe)recipe);
        else if(recipe instanceof BinaryShaderRecipe)
            return createBinaryShader((BinaryShaderRecipe)recipe);
        else if(recipe instanceof SourceShaderRecipe)
            return createSourceShader((SourceShaderRecipe)recipe);
        else if(recipe instanceof MeshRecipe)
            return createMesh((MeshRecipe)recipe);
        else
            throw new IllegalArgumentException("Attempted to create a video resource of unknown type.");
    }

    protected VideoEngine(Vector2i size) {
        this.size = size;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected VideoEngine(VideoEngine other) {
        this.size = other.size;
    }

    protected Vector2i getSize() {
        return size;
    }

    // <editor-fold desc="Events">

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onDraw();

    protected abstract void onResize();

    // </editor-fold>

    // <editor-fold desc="Recipe Processing">

    protected abstract ServerResource createTexture2D(Texture2DRecipe recipe);

    protected abstract ServerResource createBinaryShader(BinaryShaderRecipe recipe);

    protected abstract ServerResource createSourceShader(SourceShaderRecipe recipe);

    protected abstract ServerResource createMesh(MeshRecipe recipe);

    // </editor-fold>

    // <editor-fold desc="Creating and Querying Default Resources"

    protected abstract ServerResource createDefaultTexture2D();

    protected abstract ServerResource createDefaultShader();

    protected abstract ServerResource createDefaultMesh();

    protected ServerResource getTexture2DOrDefault(long handle) {
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        return resource == null ? defaultTexture2D : resource;
    }

    protected ServerResource getShaderOrDefault(long handle) {
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        return resource == null ? defaultShader : resource;
    }

    protected ServerResource getMeshOrDefault(long handle) {
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        return resource == null ? defaultMesh : resource;
    }

    // </editor-fold>

    private Vector2i size;
    private ServerResource defaultTexture2D, defaultShader, defaultMesh;
}
