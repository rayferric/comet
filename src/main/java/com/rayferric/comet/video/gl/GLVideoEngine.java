package com.rayferric.comet.video.gl;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.scenegraph.resource.video.material.BasicMaterial;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.video.BinaryShaderRecipe;
import com.rayferric.comet.server.recipe.video.MeshRecipe;
import com.rayferric.comet.server.recipe.video.SourceShaderRecipe;
import com.rayferric.comet.server.recipe.video.Texture2DRecipe;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.gl.mesh.GLMesh;
import com.rayferric.comet.video.gl.shader.GLBinaryShader;
import com.rayferric.comet.video.gl.shader.GLShader;
import com.rayferric.comet.video.gl.shader.GLSourceShader;
import com.rayferric.comet.video.gl.texture.GLTexture;
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

    // <editor-fold desc="Events">

    @Override
    protected void onStart() {
        GL.createCapabilities();

        glClearColor(0.5f, 0.4f, 0.25f, 0.0f);

        System.out.println("OpenGL video engine started.");
    }

    @Override
    protected void onStop() {
        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    protected void onDraw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Model model = (Model)Engine.getInstance().root;
        BasicMaterial material = (BasicMaterial)model.getMaterial();
        GLMesh mesh = (GLMesh)getMeshOrDefault(model.getMesh().getServerHandle());
        GLShader shader = (GLShader)getShaderOrDefault(material.getShader().getServerHandle());
        GLTexture2D texture = (GLTexture2D)getTexture2DOrDefault(material.getColorTex().getServerHandle());

        if(mesh != null && shader != null && texture != null) {
            glActiveTexture(GL_TEXTURE0);
            texture.bind();

            shader.bind();

            mesh.bind();
            glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        }

        glFlush();
    }

    @Override
    protected void onResize() {
        glViewport(0, 0, getSize().getX(), getSize().getY());
        System.out.println(getSize());
    }

    // </editor-fold>

    // <editor-fold desc="Recipe Processing">

    @Override
    protected ServerResource createTexture2D(Texture2DRecipe recipe) {
        return new GLTexture2D(recipe.getData(), recipe.getSize(), recipe.getFormat(), recipe.getFilter());
    }

    @Override
    protected ServerResource createBinaryShader(BinaryShaderRecipe recipe) {
        return new GLBinaryShader(recipe.getVertBin(), recipe.getFragBin());
    }

    @Override
    protected ServerResource createSourceShader(SourceShaderRecipe recipe) {
        return new GLSourceShader(recipe.getVertSrc(), recipe.getFragSrc());
    }

    @Override
    protected ServerResource createMesh(MeshRecipe recipe) {
        return new GLMesh(recipe.getVertices(), recipe.getIndices());
    }

    // </editor-fold>

    // <editor-fold desc="Creating Default Resources"

    @Override
    protected ServerResource createDefaultTexture2D() {
        return null;
    }

    @Override
    protected ServerResource createDefaultShader() {
        return null;
    }

    @Override
    protected ServerResource createDefaultMesh() {
        return null;
    }

    // </editor-fold>
}
