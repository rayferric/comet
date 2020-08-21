package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.recipe.ServerRecipe;
import com.rayferric.comet.video.gl.GLVideoEngine;
import com.rayferric.comet.video.gl.GLWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VideoServer extends Server {
    /**
     * Creates a new video server.
     * • Must only be called from the main thread.
     *
     * @param info engine configuration supplying video API, window title, and window size
     */
    public VideoServer(EngineInfo info) {
        VideoAPI api = info.getVideoApi();
        this.api.set(api);
        if(api == VideoAPI.OPENGL) {
            window.set(new GLWindow(info.getTitle(), info.getWindowSize()));
            videoEngine = new GLVideoEngine(getWindow().getFramebufferSize());
        }
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    @Override
    public void destroy() {
        getWindow().destroy();
    }

    /**
     * Returns the API that's currently used by the server.<br>
     * • May be called from any thread.
     *
     * @return current API
     */
    public VideoAPI getApi() {
        return api.get();
    }

    /**
     * Pauses the server and changes the API.<br>
     * • Will reload all video resources.<br>
     * • Must only be called from the main thread.
     *
     * @param api new API, must not be null
     */
    public void setApi(VideoAPI api) {
        this.api.set(api);

        stop();

        List<Resource> videoResources = new ArrayList<>();
        for(Resource resource : Engine.getInstance().snapLoadedResources())
            if(resource instanceof VideoResource)
                videoResources.add(resource);

        for(Resource resource : videoResources)
            resource.unload();

        resourceCreationPaused = true;
        start();
        waitForDestructionQueue();
        stop();
        resourceCreationPaused = false;

        Window oldWindow = getWindow();
        if(api == VideoAPI.OPENGL) {
            window.set(new GLWindow(oldWindow));
            videoEngine = new GLVideoEngine(videoEngine);
        }
        oldWindow.destroy();

        for(Resource resource : videoResources)
            resource.load();

        start();
    }

    public Window getWindow() {
        return window.get();
    }

    @Override
    protected void onStart() {
        Window.makeCurrent(getWindow());
        videoEngine.start();
    }

    @Override
    protected void onLoop() {
        videoEngine.resize(getWindow().getFramebufferSize());
        getWindow().swapBuffers();
        videoEngine.draw();
    }

    @Override
    protected void onStop() {
        videoEngine.stop();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(ServerRecipe recipe) {
        return videoEngine.resourceFromRecipe(recipe);
    }

    private final AtomicReference<VideoAPI> api = new AtomicReference<>();
    private final AtomicReference<Window> window = new AtomicReference<>();
    private VideoEngine videoEngine;
}
