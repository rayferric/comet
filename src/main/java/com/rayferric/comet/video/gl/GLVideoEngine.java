package com.rayferric.comet.video.gl;

import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL45.*;

public class GLVideoEngine implements VideoEngine {
    @Override
    public void init() {
        GL.createCapabilities();
        glClearColor(0.25f, 0.4f, 0.5f, 0.0f);
        glFlush();
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public ServerResource createTexture(Texture.InternalRecipe recipe) {
        return new GLTexture(recipe);
    }
}
