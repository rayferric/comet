package com.rayferric.comet.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.video.buffer.UniformBuffer;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.util.VideoInfo;
import com.rayferric.comet.video.util.texture.TextureFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A cross-API video interface.<br>
 * • Used internally by {@link VideoServer}.<br>
 * • Is not thread safe.
 */
public abstract class VideoEngine {
    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    // <editor-fold desc="Internal Server API">

    public void destroy() {
        defaultTexture2D.destroy();

        onStop();
    }

    public void draw() {
        onDraw();
    }

    public void update(Vector2i size, boolean vSync) {
        if(!size.equals(this.size)) {
            this.size = size;
            onResize();
            // Ensures V-Sync is updated when going fullscreen:
            onVSyncUpdate();
        }
        if(this.vSync != vSync) {
            this.vSync = vSync;
            onVSyncUpdate();
        }
    }

    // </editor-fold>

    // <editor-fold desc="Internal API">

    public abstract ServerResource createBinaryShader(ByteBuffer vertBin, ByteBuffer fragBin);

    public abstract ServerResource createGeometry(GeometryData data);

    public abstract ServerResource createSourceShader(String vertSrc, String fragSrc);

    public abstract ServerResource createTexture2D(Buffer data, Vector2i size, TextureFormat format, boolean filter);

    public abstract ServerResource createUniformBuffer(int size);

    // </editor-fold>

    protected VideoEngine(Vector2i size, boolean vSync) {
        this.size = size;
        this.vSync = vSync;

        onStart();
        onResize();
        onVSyncUpdate();

        defaultTexture2D = createDefaultTexture2D();
    }

    protected Vector2i getSize() {
        return size;
    }

    protected boolean getVSync() { return vSync; }

    // <editor-fold desc="Events">

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onDraw();

    protected abstract void onResize();

    protected abstract void onVSyncUpdate();

    // </editor-fold>

    // <editor-fold desc="Creating and Querying Default Resources"

    protected abstract ServerResource createDefaultTexture2D();

    protected ServerResource getServerGeometryOrNull(Geometry geometry) {
        if(geometry == null || !geometry.isLoaded()) return null;
        long handle = geometry.getServerHandle();
        return Engine.getInstance().getVideoServer().getServerResource(handle);
    }

    protected ServerResource getServerTexture2DOrDefault(Texture texture) {
        if(texture == null || !texture.isLoaded()) return defaultTexture2D;
        long handle = texture.getServerHandle();
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        if(resource == null) return defaultTexture2D;
        return resource;
    }

    protected ServerResource getServerShaderOrNull(Shader shader) {
        if(shader == null || !shader.isLoaded()) return null;
        long handle = shader.getServerHandle();
        return Engine.getInstance().getVideoServer().getServerResource(handle);
    }

    protected ServerResource getServerUniformBufferOrNull(UniformBuffer uniformBuffer) {
        if(uniformBuffer == null || !uniformBuffer.isLoaded()) return null;
        long handle = uniformBuffer.getServerHandle();
        return Engine.getInstance().getVideoServer().getServerResource(handle);
    }

    // </editor-fold>

    private Vector2i size;
    private boolean vSync = true;

    private final ServerResource defaultTexture2D;
}
