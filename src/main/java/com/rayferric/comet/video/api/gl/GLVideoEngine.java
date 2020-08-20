package com.rayferric.comet.video.api.gl;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.video.BinaryShaderRecipe;
import com.rayferric.comet.server.recipe.video.SourceShaderRecipe;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;
import com.rayferric.comet.video.api.VideoEngine;
import com.rayferric.comet.video.api.gl.shader.GLBinaryShader;
import com.rayferric.comet.video.api.gl.shader.GLSourceShader;
import com.rayferric.comet.video.api.gl.texture.GLTexture2D;
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

        glClearColor(0.5f, 0.4f, 0.25f, 0.0f);

        System.out.println("OpenGL video engine started.");
    }

    @Override
    public void onStop() {
        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    public void onDraw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glColor3d(0.7, 0.1, 0.2);
        glBegin(GL_TRIANGLES);
        glVertex3d(-0.5, -0.5, 0);
        glVertex3d(0.5, -0.5, 0);
        glVertex3d(0, 0.5, 0);
        glEnd();
        glFlush();
    }

    @Override
    public void onResize(Vector2i size) {
        glViewport(0, 0, size.getX(), size.getY());
    }

    @Override
    public ServerResource createTexture2D(Texture2DRecipe recipe) {
        return new GLTexture2D(recipe.getData(), recipe.getSize(), recipe.getFormat(), recipe.getFilter());
    }

    @Override
    public ServerResource createBinaryShader(BinaryShaderRecipe recipe) {
        return new GLBinaryShader(recipe.getVertBin(), recipe.getFragBin());
    }

    @Override
    public ServerResource createSourceShader(SourceShaderRecipe recipe) {
        return new GLSourceShader(recipe.getVertSrc(), recipe.getFragSrc());
    }

    @Override
    protected ServerResource createDefaultTexture2D() {
        return null;
    }

    @Override
    protected ServerResource createDefaultShader() {
        return null;
    }
}
