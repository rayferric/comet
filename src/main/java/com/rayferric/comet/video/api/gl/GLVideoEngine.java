package com.rayferric.comet.video.api.gl;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.ResourceLoader;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.api.gl.buffer.GLUniformBuffer;
import com.rayferric.comet.video.api.gl.query.GLTimerQuery;
import com.rayferric.comet.video.util.VideoInfo;
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
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.*;
import static org.lwjgl.opengl.ATIMeminfo.*;
import static org.lwjgl.opengl.WGLAMDGPUAssociation.*;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(Vector2i size, boolean vSync) {
        super(size, vSync);
    }

    // <editor-fold desc="Creating Video Resources">

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

        String deviceVendor = glGetString(GL_VENDOR);
        String apiVersion = glGetString(GL_VERSION);
        if(apiVersion != null && !apiVersion.toUpperCase().startsWith("OPEN") && !apiVersion.toUpperCase().startsWith("GL"))
            apiVersion = "OpenGL " + apiVersion;
        String shaderVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
        if(shaderVersion != null && !shaderVersion.toUpperCase().startsWith("OPEN") && !shaderVersion.toUpperCase().startsWith("GL"))
            shaderVersion = "GLSL " + shaderVersion;
        VideoInfo info = Engine.getInstance().getVideoServer().getVideoInfo();
        info.setDeviceVendor(deviceVendor);
        info.setDeviceModel(glGetString(GL_RENDERER));
        info.setApiVersion(apiVersion);
        info.setShaderVersion(shaderVersion);
        info.setTotalVRam(queryTotalVRam(deviceVendor));

        glClearColor(0.08F, 0.08F, 0.1F, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        cpuTimer = new Timer();
        gpuTimer = new GLTimerQuery();
        frameUBO = new GLUniformBuffer(FRAME_UBO_BYTES);
        modelUBO = new GLUniformBuffer(MODEL_UBO_BYTES);
        drawQuad = new GLGeometry(GeometryGenerator.genPlane(new Vector2f(2)));
        ByteBuffer depthShaderBinary = ResourceLoader.readBinaryFileToNativeBuffer(true, "shaders/depth.vert.spv");
        depthShader = new GLBinaryShader(depthShaderBinary, null);
        MemoryUtil.memFree(depthShaderBinary);

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
        drawQuad.destroy();
        depthShader.destroy();

        System.out.println("OpenGL video engine stopped.");
    }

    @Override
    protected void onDraw() {
        if(gpuTimer.hasResult()) {
            double gpuDelta = gpuTimer.read();
            Engine.getInstance().getProfiler().getGpuAccumulator().accumulate(gpuDelta);
            gpuTimer.begin();
        }

        // Drawing code:

        glClear(GL_COLOR_BUFFER_BIT);

        Vector2i size = getSize();
        float ratio = (float)size.getX() / size.getY();
        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            Camera camera = layer.getCamera();
            Matrix4f projectionMatrix = (camera == null ? Matrix4f.IDENTITY : camera.getProjection(ratio));
            Matrix4f viewMatrix = (camera == null ? Matrix4f.IDENTITY : camera.getGlobalTransform().getMatrix().inverse());
            updateFrameUBO(projectionMatrix, viewMatrix);

            Frustum frustum = new Frustum(projectionMatrix.mul(viewMatrix));

            LayerIndex index = layer.getIndex();
            List<Model> allModels = index.getModels();
            List<Model> visibleModels = new ArrayList<>(allModels.size());

            // <editor-fold desc="Early Z-Pass">

            glColorMask(false, false, false, false);
            glDepthMask(true);
            glDepthFunc(GL_LESS);
            glClear(GL_DEPTH_BUFFER_BIT);

            for(Model model : allModels) {
                if(!model.isVisible()) continue;

                Matrix4f modelMatrix = model.getGlobalTransform().getMatrix();
                updateModelUBO(modelMatrix);

                boolean isAnyMeshVisible = false;
                for(Mesh mesh : model.snapMeshes()) {
                    Material material = mesh.getMaterial();
                    if(material == null) continue;

                    GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(mesh.getGeometry());
                    if(glGeometry == null) continue;

                    AABB aabb = glGeometry.getAabb();
                    Vector3f min = modelMatrix.mul(aabb.getMin(), 1);
                    Vector3f max = modelMatrix.mul(aabb.getMax(), 1);
                    Vector3f origin = min.add(max).mul(0.5F);
                    float radius = min.distance(max) * 0.5F;
                    // aabb.getOrigin(modelMatrix), aabb.getBoundingRadius(modelMatrix)
                    if(!frustum.containsSphere(new Vector3f(-1), 2)) {
                        System.out.println("Rejecting: " + model.getName() + " " + frustum.distanceToPoint(new Vector3f(-1)));
                        continue;
                    }

                    if(!isAnyMeshVisible) {
                        visibleModels.add(model);
                        isAnyMeshVisible = true;
                    }

                    if(material.isTranslucent()) continue;

                    if(material.hasCulling())
                        glEnable(GL_CULL_FACE);
                    else
                        glDisable(GL_CULL_FACE);

                    depthShader.bind();

                    glGeometry.bind();
                    glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
                }
            }

            // </editor-fold>

            // <editor-fold desc="Lighting Pass">

            glColorMask(true, true, true, true);
            glDepthMask(false);
            glDepthFunc(GL_LEQUAL);

            List<TranslucentMesh> translucentMeshes = new ArrayList<>();

            for(Model model : visibleModels) {
                if(!model.isVisible()) continue;

                Matrix4f modelMatrix = model.getGlobalTransform().getMatrix();
                updateModelUBO(modelMatrix);

                for(Mesh mesh : model.snapMeshes()) {
                    Material material = mesh.getMaterial();
                    if(material == null) continue;

                    GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(mesh.getGeometry());
                    if(glGeometry == null) continue;

                    // Here the geometry would be frustum tested, but culling individual
                    // meshes of a mostly visible model is pointless and wasteful

                    if(material.isTranslucent()) {
                        Vector3f meshOriginViewSpace = glGeometry.getAabb().getOrigin(viewMatrix.mul(modelMatrix));
                        float cameraDist = meshOriginViewSpace.length();
                        TranslucentMesh translucentMesh = new TranslucentMesh(mesh, modelMatrix, cameraDist);
                        translucentMeshes.add(translucentMesh);
                        continue;
                    }

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

                    glGeometry.bind();

                    glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
                }
            }

            // Translucency must be drawn back-to-front.
            translucentMeshes.sort(Collections.reverseOrder());

            for(TranslucentMesh translucentMesh : translucentMeshes) {
                Matrix4f modelMatrix = translucentMesh.getModelMatrix();
                updateModelUBO(modelMatrix);

                Mesh mesh = translucentMesh.getMesh();
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

            // </editor-fold>
        }

        // End of drawing code.

        gpuTimer.end();
        glFlush();

        VideoInfo info = Engine.getInstance().getVideoServer().getVideoInfo();
        info.setFreeVRam(queryFreeVRam(info.getDeviceVendor()));

        double cpuDelta = cpuTimer.getElapsed();
        cpuTimer.reset();
        Engine.getInstance().getProfiler().getCpuAccumulator().accumulate(cpuDelta);

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

    private static class TranslucentMesh implements Comparable<TranslucentMesh> {
        public TranslucentMesh(Mesh mesh, Matrix4f modelMatrix, float cameraDistance) {
            this.mesh = mesh;
            this.modelMatrix = modelMatrix;
            this.cameraDistance = cameraDistance;
        }

        @Override
        public int compareTo(TranslucentMesh other) {
            return cameraDistance > other.cameraDistance ? 1 : -1;
        }

        public Mesh getMesh() {
            return mesh;
        }

        public Matrix4f getModelMatrix() {
            return modelMatrix;
        }

        private final Mesh mesh;
        private final Matrix4f modelMatrix;
        private final float cameraDistance;
    }

    private static final int FRAME_UBO_BYTES = 2 * Matrix4f.BYTES;
    private static final int MODEL_UBO_BYTES = Matrix4f.BYTES;

    private Timer cpuTimer;
    private GLTimerQuery gpuTimer;
    private GLUniformBuffer frameUBO;
    private GLUniformBuffer modelUBO;
    private GLGeometry drawQuad;
    private GLShader depthShader;

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

    private List<Model> drawModels(List<Model> models, boolean opaqueOnly) {
        int totalVertices = 0;
        int totalIndices = 0;

        List<Model> translucentModels = new ArrayList<>(models.size());

        for(Model model : models) {
            if(!model.isVisible()) continue;

            Matrix4f modelMatrix = model.getGlobalTransform().getMatrix();
            updateModelUBO(modelMatrix);

            boolean registeredTranslucent = false;
            for(Mesh mesh : model.snapMeshes()) {
                Material material = mesh.getMaterial();
                if(material == null) continue;

                if(opaqueOnly && material.isTranslucent() && !registeredTranslucent) {
                    translucentModels.add(model);
                    registeredTranslucent = true;
                    continue;
                }

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

                int indexCount = glGeometry.getIndexCount();
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
                totalVertices += glGeometry.getVertexCount();
                totalIndices += indexCount;
            }
        }

        VideoInfo videoInfo = Engine.getInstance().getVideoServer().getVideoInfo();
        videoInfo.setVertexCount(totalVertices);
        videoInfo.setTriangleCount(totalIndices / 3);

        return translucentModels;
    }

    private int queryTotalVRam(String deviceVendor) {
        deviceVendor = deviceVendor.toUpperCase();

        if(deviceVendor.contains("NVIDIA"))
            return glGetInteger(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) * 1024;
        else if(deviceVendor.contains("AMD") || deviceVendor.contains("ATI")) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer ids = stack.mallocInt(1);
                IntBuffer mem = stack.mallocInt(1);
                wglGetGPUIDsAMD(ids);
                wglGetGPUInfoAMD(ids.get(0), WGL_GPU_RAM_AMD, GL_UNSIGNED_INT, mem);
                return mem.get(0) * 1024;
            }
        } else
            return -1;
    }

    private int queryFreeVRam(String deviceVendor) {
        deviceVendor = deviceVendor.toUpperCase();

        if(deviceVendor.contains("NVIDIA"))
            return glGetInteger(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX) * 1024;
        else if(deviceVendor.contains("AMD") || deviceVendor.contains("ATI"))
            return glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) * 1024;
        else
            return -1;
    }
}
