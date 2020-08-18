package com.rayferric.comet.server;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.Texture;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.Window;

public class VideoServer extends Server {
    public VideoServer(Window window, VideoEngine videoEngine) {
        this.window = window;
        this.videoEngine = videoEngine;
        videoEngine.setVideoServer(this);
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    @Override
    protected void process() {
        Window.makeCurrent(window);
        videoEngine.start();

        while(running.get()) {
            Vector2i fbSize = window.getFramebufferSize();
            if(!fbSize.equals(videoEngine.getSize()))
                videoEngine.resize(fbSize);

            videoEngine.draw();

            // Command buffer is flushed and now we process the resources while swap timeout passes
            createNextPendingServerResource();
            freeNextPendingServerResource();

            window.swapBuffers();
        }

        videoEngine.stop();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        if(recipe instanceof Texture.ServerRecipe)
            return videoEngine.createTexture((Texture.ServerRecipe)recipe);
        else
            throw new RuntimeException(
                    "Commissioned creation of an incompatible type of resource. (This is not a recipe of a video resource.)");
    }

    private final Window window;
    private final VideoEngine videoEngine;
}
