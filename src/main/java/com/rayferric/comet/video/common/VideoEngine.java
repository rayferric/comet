package com.rayferric.comet.video.common;

import com.rayferric.comet.util.AutoMap;

import java.nio.ByteBuffer;

public abstract class VideoEngine {
    public abstract void draw();
    public abstract long createTexture(ByteBuffer data, int width, int height);

    public void freeResource(long handle) {
        VideoResource resource = resources.remove(handle);
        if(resource != null)
            resource.free();
        else
            throw new IllegalStateException("Requested to free a non-existent resource.");
    }

    public void reloadResources() {
        AutoMap<VideoResource> resourcesSnapshot = new AutoMap<>(resources);
        resources.forEach((handle, resource) -> {
            resource.free();
        });
    }

    protected AutoMap<VideoResource> resources = new AutoMap<>();
}
