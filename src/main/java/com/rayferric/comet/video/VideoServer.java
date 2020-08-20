package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.ServerRecipe;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.api.VideoEngine;
import com.rayferric.comet.video.api.Window;
import com.rayferric.comet.video.api.gl.GLVideoEngine;
import com.rayferric.comet.video.api.gl.GLWindow;

import java.util.ArrayList;
import java.util.List;

public class VideoServer extends Server {
    public VideoServer(EngineInfo info) {
        if(info.getVideoApi() == VideoAPI.OPENGL) {
            window = new GLWindow(info.getTitle(), info.getWindowSize());
            videoEngine = new GLVideoEngine(window.getFramebufferSize());
        } else
            throw new IllegalArgumentException("Requested use of non-existent API.");
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    @Override
    public void destroy() {
        window.destroy();
    }

    /**
     * Pauses the server and reloads the API.<br>
     * • Will reload all video resources.
     * • Must only be called from the main thread.
     *
     * @param api new API, must not be null
     */
    public void reloadApi(VideoAPI api) {
        stop();

        List<Resource> videoResources = new ArrayList<>();
        for(Resource resource : Engine.getInstance().snapLoadedResources())
            if(resource instanceof VideoResource)
                videoResources.add(resource);

        for(Resource resource : videoResources)
            resource.unload();

        setResourceCreationPaused(true);
        start();
        waitForDestructionQueue();
        stop();
        setResourceCreationPaused(false);

        Window oldWindow = window; // TODO make thread-safe (or not? does window really expose any thread-safe funcs?)
        window = new GLWindow(oldWindow);
        oldWindow.destroy();
        videoEngine = new GLVideoEngine(videoEngine);

        start();

        for(Resource resource : videoResources)
            resource.load();
    }

    public Window getWindow() {
        return window;
    }

    @Override
    protected void onStart() {
        Window.makeCurrent(window);
        videoEngine.onStart();
        videoEngine.createDefaultResources();
    }

    @Override
    protected void onLoop() {
        window.swapBuffers();
        videoEngine.setSize(window.getFramebufferSize());
        videoEngine.onDraw();
    }

    @Override
    protected void onStop() {
        videoEngine.onStop();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(ServerRecipe recipe) {
        return videoEngine.resourceFromRecipe(recipe);
    }

    private Window window;
    private VideoEngine videoEngine;
}
