package com.rayferric.comet.video.gl.texture;

import com.rayferric.comet.server.ServerResource;

import static org.lwjgl.opengl.GL45.*;

public abstract class GLTexture implements ServerResource {
    public GLTexture() {
        System.out.println("Creating GLTexture...");
        handle = glGenTextures();
        bind();
    }

    @Override
    public String toString() {
        return String.format("GLTexture{handle=%s}", handle);
    }

    @Override
    public void destroy() {
        System.out.println("Destroying GLTexture...");
        glDeleteTextures(handle);
    }

    public abstract void bind();

    public int getHandle() {
        return handle;
    }

    protected final int handle;
}
