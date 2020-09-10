package com.rayferric.comet.video.api.gl;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.common.material.Material;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.common.Mesh;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.ResourceLoader;
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

        gpuTimer = new GLTimerQuery();
        frameUBO = new GLUniformBuffer(FRAME_UBO_BYTES);
        modelUBO = new GLUniformBuffer(MODEL_UBO_BYTES);
        drawQuad = new GLGeometry(GeometryGenerator.genPlane(new Vector2f(2)));
        ByteBuffer depthShaderBinary = ResourceLoader.readBinaryFileToNativeBuffer(true, "shaders/depth.vert.spv");
        depthShader = new GLBinaryShader(depthShaderBinary, null);
        MemoryUtil.memFree(depthShaderBinary);

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

        int totalVerticesDrawn = 0, totalIndicesDrawn = 0;

        Vector2i size = getSize();
        float ratio = (float)size.getX() / size.getY();
        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            Camera camera = layer.getCamera();
            if(camera == null) continue;
            Matrix4f projectionMatrix = camera.getProjection(ratio);
            Matrix4f viewMatrix = camera.getGlobalTransform().inverse();
            updateFrameUBO(projectionMatrix, viewMatrix);

            Frustum frustum = new Frustum(projectionMatrix.mul(viewMatrix));

            LayerIndex index = layer.getIndex();
            List<Model> allModels = index.getModels();
            opaqueModels.clear();
            translucentMeshes.clear();

            // <editor-fold desc="Early Z-Pass + Translucency Sorting + Vertex Counting">

            glColorMask(false, false, false, false);
            glDepthMask(true);
            glDepthFunc(GL_LESS);
            glClear(GL_DEPTH_BUFFER_BIT);

            for(Model model : allModels) {
                if(!model.isVisible()) continue;

                Matrix4f modelMatrix = model.getGlobalTransform();
                updateModelUBO(modelMatrix);

                OpaqueModel opaqueModel = null;

                List<Mesh> meshes = model.snapMeshes();
                for(Mesh mesh : meshes) {
                    Material material = mesh.getMaterial();
                    if(material == null) continue;

                    GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(mesh.getGeometry());
                    if(glGeometry == null) continue;

                    AABB aabb = glGeometry.getAabb().transform(modelMatrix);
                    Vector3f meshOriginWorldSpace = aabb.getOrigin();

                    // World Space Frustum Culling
                    if(!frustum.containsSphere(meshOriginWorldSpace, aabb.getBoundingRadius()))
                        continue;

                    // We only want opaque geometry in the depth buffer.
                    // We also want to calculate distance to the camera to sort the translucency list later on.
                    if(material.isTranslucent()) {
                        TranslucentMesh translucentMesh = new TranslucentMesh();
                        translucentMesh.mesh = mesh;
                        translucentMesh.modelMatrix = modelMatrix;
                        translucentMesh.cameraDistance = viewMatrix.mul(meshOriginWorldSpace, 1).length();
                        translucentMeshes.add(translucentMesh);
                        continue;
                    }

                    // Identify opaque geometry just like we did with translucent meshes.
                    if(opaqueModel == null)
                        opaqueModels.add(opaqueModel = new OpaqueModel(meshes.size(), modelMatrix));
                    opaqueModel.meshes.add(mesh);

                    if(material.hasCulling())
                        glEnable(GL_CULL_FACE);
                    else
                        glDisable(GL_CULL_FACE);

                    depthShader.bind();

                    glGeometry.bind();
                    glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
                }
            }

            // Translucency must be drawn back-to-front.
            translucentMeshes.sort(Collections.reverseOrder());

            // </editor-fold>

            // <editor-fold desc="Lighting Pass">

            glColorMask(true, true, true, true);
            glDepthMask(false);
            glDepthFunc(GL_LEQUAL);

            for(OpaqueModel opaqueModel : opaqueModels) {
                updateModelUBO(opaqueModel.modelMatrix);
                for(Mesh mesh : opaqueModel.meshes) {
                    Material material = mesh.getMaterial();
                    if(material == null) continue; // The material could've been changed on another thread.

                    // We already have the translucentMeshes list that holds all the translucent
                    // meshes in the right order, therefore we can simply skip processing them here.
                    if(material.isTranslucent()) continue;

                    GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(mesh.getGeometry());
                    if(glGeometry == null) continue; // The geometry could've been changed on another thread too.

                    // Here, the geometry could be frustum tested, but hiding individual
                    // meshes of a mostly visible model is a waste of CPU time.

                    if(!useMaterial(material)) continue;

                    glGeometry.bind();
                    totalVerticesDrawn += glGeometry.getVertexCount();
                    totalIndicesDrawn += glGeometry.getIndexCount();

                    glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
                }
            }

            for(TranslucentMesh translucentMesh : translucentMeshes) {
                updateModelUBO(translucentMesh.modelMatrix);

                Material material = translucentMesh.mesh.getMaterial();
                if(material == null) continue;
                if(!useMaterial(material)) continue;

                GLGeometry glGeometry = (GLGeometry)getServerGeometryOrNull(translucentMesh.mesh.getGeometry());
                if(glGeometry == null) continue;
                glGeometry.bind();
                totalVerticesDrawn += glGeometry.getVertexCount();
                totalIndicesDrawn += glGeometry.getIndexCount();

                glDrawElements(GL_TRIANGLES, glGeometry.getIndexCount(), GL_UNSIGNED_INT, 0);
            }

            // </editor-fold>
        }

        VideoInfo videoInfo = Engine.getInstance().getVideoServer().getVideoInfo();
        videoInfo.setVertexCount(totalVerticesDrawn);
        videoInfo.setTriangleCount(totalIndicesDrawn / 3);

        // End of drawing code.

        gpuTimer.end();
        glFlush();

        VideoInfo info = Engine.getInstance().getVideoServer().getVideoInfo();
        info.setFreeVRam(queryFreeVRam(info.getDeviceVendor()));

        int error = glGetError();
        if(error != GL_NO_ERROR)
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

    private static class OpaqueModel {
        public OpaqueModel(int numMeshes, Matrix4f modelMatrix) {
            meshes = new ArrayList<>(numMeshes);
            this.modelMatrix = modelMatrix;
        }

        public List<Mesh> meshes;
        public Matrix4f modelMatrix;
    }

    private static class TranslucentMesh implements Comparable<TranslucentMesh> {
        public Mesh mesh;
        public Matrix4f modelMatrix;
        public float cameraDistance;

        @Override
        public int compareTo(TranslucentMesh other) {
            return cameraDistance > other.cameraDistance ? 1 : -1;
        }
    }

    private static final int FRAME_UBO_BYTES = 2 * Matrix4f.BYTES;
    private static final int MODEL_UBO_BYTES = Matrix4f.BYTES;

    private GLTimerQuery gpuTimer;
    private GLUniformBuffer frameUBO;
    private GLUniformBuffer modelUBO;
    private GLGeometry drawQuad;
    private GLShader depthShader;
    List<OpaqueModel> opaqueModels = new ArrayList<>();
    List<TranslucentMesh> translucentMeshes = new ArrayList<>();

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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean useMaterial(Material material) {
        if(material.hasCulling())
            glEnable(GL_CULL_FACE);
        else
            glDisable(GL_CULL_FACE);

        GLShader glShader = (GLShader)getServerShaderOrNull(material.getShader());
        if(glShader == null) return false;
        glShader.bind();

        GLUniformBuffer glUniformBuffer =
                (GLUniformBuffer)getServerUniformBufferOrNull(material.getUniformBuffer());
        if(glUniformBuffer == null) return false;
        if(material.popNeedsUpdate() || glUniformBuffer.popJustCreated()) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                glUniformBuffer.update(material.snapUniformData(stack));
            }
        }
        glUniformBuffer.bind(2);

        material.getTextures().forEach((binding, texture) -> {
            glActiveTexture(GL_TEXTURE0 + binding);
            ((GLTexture)getServerTexture2DOrDefault(texture)).bind();
        });

        return true;
    }

    private long queryTotalVRam(String deviceVendor) {
        deviceVendor = deviceVendor.toUpperCase();

        if(deviceVendor.contains("NVIDIA"))
            return glGetInteger(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) * 1024L;
        else if(deviceVendor.contains("AMD") || deviceVendor.contains("ATI")) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer ids = stack.mallocInt(1);
                IntBuffer mem = stack.mallocInt(1);
                wglGetGPUIDsAMD(ids);
                wglGetGPUInfoAMD(ids.get(0), WGL_GPU_RAM_AMD, GL_UNSIGNED_INT, mem);
                return mem.get(0) * 1024L;
            }
        } else
            return -1;
    }

    private long queryFreeVRam(String deviceVendor) {
        deviceVendor = deviceVendor.toUpperCase();

        if(deviceVendor.contains("NVIDIA"))
            return glGetInteger(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX) * 1024L;
        else if(deviceVendor.contains("AMD") || deviceVendor.contains("ATI"))
            return glGetInteger(GL_TEXTURE_FREE_MEMORY_ATI) * 1024L;
        else
            return -1;
    }
}
