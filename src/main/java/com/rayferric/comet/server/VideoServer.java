package com.rayferric.comet.server;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.video.common.VideoEngine;
import com.rayferric.comet.video.common.Window;

public class VideoServer extends Server {
    public VideoServer(Window window, VideoEngine videoEngine) {
        this.window = window;
        this.videoEngine = videoEngine;
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    @Override
    protected void process() {
        Window.makeCurrent(window);
        videoEngine.onStart();

        while(running.get()) {
            videoEngine.setSize(window.getFramebufferSize());

            videoEngine.onDraw();

            // Command buffer is flushed and now we process the resources while swap timeout passes
            createNextPendingResource();
            destroyNextPendingResource();

            window.swapBuffers();
        }

        videoEngine.onStop();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        return videoEngine.resourceFromRecipe(recipe);
    }

    private final Window window;
    private final VideoEngine videoEngine;
}
