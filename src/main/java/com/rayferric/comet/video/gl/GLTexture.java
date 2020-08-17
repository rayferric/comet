package com.rayferric.comet.video.gl;

import com.rayferric.comet.resources.Texture;
import com.rayferric.comet.video.common.InternalVideoResource;

import static org.lwjgl.opengl.GL11.*;

public class GLTexture implements InternalVideoResource {
    public GLTexture(Texture.InternalRecipe recipe) {
        System.out.println("Loaded OpenGL texture.");
        //handle = glGenTextures();
        //bind();
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }

    public int getHandle() {
        return handle;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    @Override
    public void free() {
        //glDeleteTextures(handle);
        System.out.println("Removed OpenGL texture.");
    }

    private int handle;
}
