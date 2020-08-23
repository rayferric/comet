package com.rayferric.comet.video.api.gl;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.api.gl.buffer.GLUniformBuffer;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.comet.video.util.texture.TextureFormat;
import com.rayferric.comet.video.api.gl.mesh.GLMesh;
import com.rayferric.comet.video.api.gl.shader.GLBinaryShader;
import com.rayferric.comet.video.api.gl.shader.GLShader;
import com.rayferric.comet.video.api.gl.shader.GLSourceShader;
import com.rayferric.comet.video.api.gl.texture.GLTexture;
import com.rayferric.comet.video.api.gl.texture.GLTexture2D;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL45.*;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(Vector2i size) {
        super(size);
    }

    public GLVideoEngine(VideoEngine other) {
        super(other);
    }

    // <editor-fold desc="Internal API">

    @Override
    public void drawModel(Model model) {
        Material material = model.getMaterial();

        GLShader glShader = (GLShader)getServerShaderOrNull(material.getShader());
        if(glShader == null) return;
        glShader.bind();

        GLUniformBuffer glUniformBuffer = (GLUniformBuffer)getServerUniformBufferOrNull(material.getUniformBuffer());
        if(glUniformBuffer == null) return;
        glUniformBuffer.bind(2);
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glUniformBuffer.nativeUpdate(material.nativeUniformData(stack));
        }

        material.getTextures().forEach((binding, texture) -> {
            glActiveTexture(GL_TEXTURE0 + binding);
            ((GLTexture)getServerTexture2DOrDefault(texture)).bind();
        });

        GLMesh glMesh = (GLMesh)getServerMeshOrNull(model.getMesh());
        if(glMesh == null) return;
        glMesh.bind();

        glDrawElements(GL_TRIANGLES, glMesh.getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public ServerResource createBinaryShader(ByteBuffer vertBin, ByteBuffer fragBin) {
        return new GLBinaryShader(vertBin, fragBin);
    }

    @Override
    public ServerResource createMesh(FloatBuffer vertices, IntBuffer indices) {
        return new GLMesh(vertices, indices);
    }

    @Override
    public ServerResource createSourceShader(String vertSrc, String fragSrc) {
        return new GLSourceShader(vertSrc, fragSrc);
    }

    @Override
    public ServerResource createTexture2D(Buffer data, Vector2i size, TextureFormat format, TextureFilter filter) {
        return new GLTexture2D(data, size, format, filter);
    }

    @Override
    public ServerResource createUniformBuffer(int size) {
        return new GLUniformBuffer(size);
    }

    // </editor-fold>

    // <editor-fold desc="Events">

    @Override
    protected void onStart() {
        GL.createCapabilities();

        glClearColor(0, 0, 0, 0);

        System.out.println("OpenGL video engine started.");
    }

    @Override
    protected void onStop() {
        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    protected void onDraw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int error = glGetError();
        if(error != 0) System.out.println("OpenGL Error: " + error);

        Engine.getInstance().root.draw(this);

        glFlush();

        if(++frames % 1000 == 0) System.out.println("Reached 1000 frames.");
    }
    private long frames = 0;

    @Override
    protected void onResize() {
        glViewport(0, 0, getSize().getX(), getSize().getY());
    }

    @Override
    protected void onVSyncUpdate() {
        if(getVSync())
            glfwSwapInterval(1);
        else
            glfwSwapInterval(0);
    }

    // </editor-fold>

    @Override
    protected ServerResource createDefaultTexture2D() {
        byte[] data = { (byte)0xFF, (byte)0xFF, (byte)0xFF };

        ByteBuffer nativeData = MemoryUtil.memAlloc(3);
        nativeData.put(data);
        nativeData.flip();
        GLTexture texture = new GLTexture2D(nativeData, new Vector2i(1), TextureFormat.RGB8, TextureFilter.NEAREST);
        MemoryUtil.memFree(nativeData);

        return texture;
    }
}
