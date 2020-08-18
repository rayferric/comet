package com.rayferric.comet.video;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.Texture;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.VideoServer;

public abstract class VideoEngine {
    public VideoEngine(Vector2i size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("VideoEngine{size=%s}", size);
    }

    public abstract void start();

    public abstract void stop();

    public abstract void draw();

    public void resize(Vector2i size) {
        this.size = size;
    }

    public abstract ServerResource createTexture(Texture.ServerRecipe recipe);

    public void setVideoServer(VideoServer videoServer) {
        this.videoServer = videoServer;
    }

    public Vector2i getSize() {
        return size;
    }

    protected Vector2i size;

    protected ServerResource getServerResource(Resource resource) {
        return videoServer.getServerResource(resource);
    }

    private VideoServer videoServer = null;
}
