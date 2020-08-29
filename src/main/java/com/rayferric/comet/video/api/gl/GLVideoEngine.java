package com.rayferric.comet.video.api.gl;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.api.gl.buffer.GLUniformBuffer;
import com.rayferric.comet.video.api.gl.query.GLTimerQuery;
import com.rayferric.comet.video.util.texture.TextureFormat;
import com.rayferric.comet.video.api.gl.geometry.GLGeometry;
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

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL43.*;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(Vector2i size, boolean vSync) {
        super(size, vSync);
    }

    // <editor-fold desc="Internal API">

    @Override
    public ServerResource createBinaryShader(ByteBuffer vertBin, ByteBuffer fragBin) {
        return new GLBinaryShader(vertBin, fragBin);
    }

    @Override
    public ServerResource createGeometry(GeometryData data) {
        return new GLGeometry(data);
    }

    @Override
    public ServerResource createSourceShader(String vertSrc, String fragSrc) {
        return new GLSourceShader(vertSrc, fragSrc);
    }

    @Override
    public ServerResource createTexture2D(Buffer data, Vector2i size, TextureFormat format, boolean filter) {
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

        glClearColor(0.08F, 0.08F, 0.1F, 0);
        glEnable(GL_DEPTH_TEST);

        cpuTimer = new Timer();
        gpuTimer = new GLTimerQuery();
        frameUBO = new GLUniformBuffer(FRAME_UBO_BYTES);
        modelUBO = new GLUniformBuffer(MODEL_UBO_BYTES);

        cpuTimer.start();
        frameUBO.bind(0);
        modelUBO.bind(1);

        System.out.println("OpenGL video engine started.");
    }

    @Override
    protected void onStop() {
        gpuTimer.destroy();
        frameUBO.destroy();
        modelUBO.destroy();

        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    protected void onDraw() {
        double cpuDelta = cpuTimer.getElapsed();
        cpuTimer.reset();
        Engine.getInstance().getProfiler().addVideoCpuTime(cpuDelta);
        if(gpuTimer.hasResult()) {
            double gpuDelta = gpuTimer.read();
            Engine.getInstance().getProfiler().addVideoGpuTime(gpuDelta);
            gpuTimer.begin();
        }

        // Drawing code:

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Vector2i size = getSize();
        float ratio = (float)size.getX() / size.getY();
        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            Camera camera = layer.getCamera();
            if(camera == null) continue;
            Matrix4f projectionMatrix = camera.getProjection(ratio);
            Matrix4f viewMatrix = camera.getGlobalTransform().getMatrix().inverse();
            updateFrameUBO(projectionMatrix, viewMatrix);

            LayerIndex index = layer.getIndex();
            for(Model model : index.getModels()) {
                Matrix4f modelMatrix = model.getGlobalTransform().getMatrix();
                updateModelUBO(modelMatrix);

                for(Mesh mesh : model.snapMeshes()) {
                    Material material = mesh.getMaterial();

                    if(material.hasCulling())
                        glEnable(GL_CULL_FACE);
                    else
                        glDisable(GL_CULL_FACE);

                    GLShader glShader = (GLShader)getServerShaderOrNull(material.getShader());
                    if(glShader == null) continue;
                    glShader.bind();

                    GLUniformBuffer glUniformBuffer =
                            (GLUniformBuffer)getServerUniformBufferOrNull(material.getUniformBuffer());
                    if(glUniformBuffer == null) continue;
                    if(material.needsUpdate() || glUniformBuffer.isJustCreated()) {
                        try(MemoryStack stack = MemoryStack.stackPush()) {
                            glUniformBuffer.update(material.snapUniformData(stack));
                        }
                    }
                    glUniformBuffer.bind(2);

                    material.getTextures().forEach((binding, texture) -> {
                        glActiveTexture(GL_TEXTURE0 + binding);
                        ((GLTexture)getServerTexture2DOrDefault(texture)).bind();
                    });

                    GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(mesh.getGeometry());
                    if(glGeometry == null) continue;
                    glGeometry.bind();

                    glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
                }
            }
        }

        // End of drawing code.

        gpuTimer.end();
        glFlush();
        int error = glGetError();
        if(error != 0)
            throw new RuntimeException("Encountered OpenGL error: " + error);
    }

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
        GLTexture texture = new GLTexture2D(nativeData, new Vector2i(1), TextureFormat.RGB8, false);
        MemoryUtil.memFree(nativeData);

        return texture;
    }

    private static final int FRAME_UBO_BYTES = 2 * Matrix4f.BYTES;
    private static final int MODEL_UBO_BYTES = Matrix4f.BYTES;

    private Timer cpuTimer;
    private GLTimerQuery gpuTimer;
    private GLUniformBuffer frameUBO;
    private GLUniformBuffer modelUBO;

    private void updateFrameUBO(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(FRAME_UBO_BYTES / Float.BYTES);
            buf.put(projectionMatrix.toArray());
            buf.put(viewMatrix.toArray());
            frameUBO.update(buf.flip());
        }
    }

    private void updateModelUBO(Matrix4f modelMatrix) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(MODEL_UBO_BYTES / Float.BYTES);
            buf.put(modelMatrix.toArray());
            modelUBO.update(buf.flip());
        }
    }
}
