package com.rayferric.comet.video.gl;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.video.ImageTexture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.common.VideoEngine;
import com.rayferric.comet.video.gl.texture.GLTexture2DImage;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL45.*;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(Vector2i size) {
        super(size);
    }

    @Override
    public void start() {
        GL.createCapabilities();
        glClearColor(0.25f, 0.4f, 0.5f, 0.0f);
        System.out.println("OpenGL video engine started.");
    }

    @Override
    public void stop() {
        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glFlush();
    }

    @Override
    public void resize(Vector2i size) {
        super.resize(size);
        System.out.println("Resized OpenGL video engine: " + size);
    }

    @Override
    public ServerResource createImageTexture(ImageTexture.ServerRecipe recipe) {
        return new GLTexture2DImage(recipe);
    }
}
