package com.rayferric.comet.server;

import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.Window;

public class VideoServer extends Server {
    public VideoServer(Window window, VideoEngine videoEngine) {
        this.window = window;
        this.videoEngine = videoEngine;
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        if(isRunning())
            throw new RuntimeException("Unable to change window object while the server is running.");
        this.window = window;
    }

    public VideoEngine getVideoEngine() {
        return videoEngine;
    }

    public void setVideoEngine(VideoEngine videoEngine) {
        if(isRunning())
            throw new RuntimeException("Unable to change video engine object while the server is running.");
        this.videoEngine = videoEngine;
    }

    @Override
    protected void onStart() {
        Window.makeCurrent(window);
        videoEngine.onStart();
    }

    @Override
    protected void onLoop() {
        window.swapBuffers();

        videoEngine.setSize(window.getFramebufferSize());

        // It's important to draw at the end, so server can do processing while swap delay passes.
        // Drawing method should flush the command buffer now.
        videoEngine.onDraw();
    }

    @Override
    protected void onStop() {
        videoEngine.onStop();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(Resource.ServerRecipe recipe) {
        return videoEngine.resourceFromRecipe(recipe);
    }

    private Window window;
    private VideoEngine videoEngine;
}
