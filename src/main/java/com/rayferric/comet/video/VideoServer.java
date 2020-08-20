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
            window = new GLWindow(info.getTitle(), info.getWindowSize());
            videoEngine = new GLVideoEngine(window.getFramebufferSize());
        }
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

        setResourceCreationPaused(true);
        start();
        waitForDestructionQueue();
        stop();
        setResourceCreationPaused(false);

        Window oldWindow = window; // TODO make thread-safe (or not? does window really expose any thread-safe funcs?)
        if(api == VideoAPI.OPENGL) {
            window = new GLWindow(oldWindow);
            videoEngine = new GLVideoEngine(videoEngine);
        }
        oldWindow.destroy();

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

    private final AtomicReference<VideoAPI> api = new AtomicReference<>();
    private Window window;
    private VideoEngine videoEngine;
}
