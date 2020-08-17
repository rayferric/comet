package com.rayferric.comet.video.gl;

import com.rayferric.comet.video.common.VideoEngine;

import java.nio.ByteBuffer;

public class GLVideoEngine extends VideoEngine {
    @Override
    public void draw() {

    }

    @Override
    public long createTexture(ByteBuffer data, int width, int height) {
        final long key = resources.put(null);
        // TODO Enqueue render thread task to fill this null
        return key;
    }
}
