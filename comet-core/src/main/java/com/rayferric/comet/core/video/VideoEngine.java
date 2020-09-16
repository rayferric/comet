package com.rayferric.comet.core.video;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.video.util.texture.TextureFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * A cross-API video interface.<br>
 * • Used internally by {@link VideoServer}.<br>
 * • Is not thread safe.
 */
public abstract class VideoEngine {
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

    public abstract ServerResource createMesh(MeshData data);

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

    protected ServerResource getServerResourceOrNull(VideoResource resource) {
        if(resource == null) return null;
        long handle = resource.getServerHandle();
        return Engine.getInstance().getVideoServer().getServerResource(handle);
    }

    protected ServerResource getServerTexture2DOrDefault(Texture texture) {
        if(texture == null) return defaultTexture2D;
        long handle = texture.getServerHandle();
        ServerResource resource = Engine.getInstance().getVideoServer().getServerResource(handle);
        if(resource == null) return defaultTexture2D;
        return resource;
    }

    // </editor-fold>

    private Vector2i size;
    private boolean vSync = true;

    private final ServerResource defaultTexture2D;
}
