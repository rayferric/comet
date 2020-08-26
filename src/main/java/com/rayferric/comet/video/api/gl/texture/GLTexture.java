package com.rayferric.comet.video.api.gl.texture;

import com.rayferric.comet.server.ServerResource;

import static org.lwjgl.opengl.GL45.glDeleteTextures;
import static org.lwjgl.opengl.GL45.glGenTextures;

public abstract class GLTexture implements ServerResource {
    public GLTexture() {
        handle = glGenTextures();
        bind();
    }

    @Override
    public String toString() {
        return String.format("GLTexture{handle=%s}", handle);
    }

    @Override
    public void destroy() {
        glDeleteTextures(handle);
    }

    public abstract void bind();

    public int getHandle() {
        return handle;
    }

    protected final int handle;
}
