package com.rayferric.comet.video.gl;

import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.server.ServerResource;

import static org.lwjgl.opengl.GL45.*;

public class GLTexture implements ServerResource {
    public GLTexture(Texture.InternalRecipe recipe) {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, recipe.getChannels() == 4 ? GL_RGBA8 : GL_RGB8, recipe.getWidth(),
                recipe.getHeight(), 0, recipe.getChannels() == 4 ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE,
                recipe.getData());
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        GLTexture other = (GLTexture)o;
        return handle == other.handle;
    }

    @Override
    public String toString() {
        return String.format("GLTexture{handle=%s}", handle);
    }

    public int getHandle() {
        return handle;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    @Override
    public void free() {
        glDeleteTextures(handle);
    }

    private final int handle;
}
