package com.rayferric.comet.video.gl;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.gl.texture.GLTexture2D;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL45.*;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(Vector2i size) {
        super(size);
    }

    public GLVideoEngine(VideoEngine other) {
        super(other);
    }

    @Override
    public void onStart() {
        GL.createCapabilities();
        glClearColor(0.4f, 0.25f, 0.5f, 0.0f);
        System.out.println("OpenGL video engine started.");
    }

    @Override
    public void onStop() {
        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    public void onDraw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glFlush();
    }

    @Override
    public void onResize(Vector2i size) {

    }

    @Override
    public ServerResource createTexture(Texture.ServerRecipe recipe) {
        return new GLTexture2D(recipe);
    }
}
