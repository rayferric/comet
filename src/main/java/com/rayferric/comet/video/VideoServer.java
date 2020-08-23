package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.api.gl.GLVideoEngine;
import com.rayferric.comet.video.api.gl.GLWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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

        if(api == VideoAPI.OPENGL) {
            window.set(new GLWindow(info.getWindowTitle(), info.getWindowSize(), info.getWindowMode()));
            videoEngine = new GLVideoEngine(getWindow().getFramebufferSize());
        }

        this.api.set(api);
        this.vSync.set(info.hasVSync());
    }

    @Override
    public String toString() {
        return String.format("VideoServer{window=%s, videoEngine=%s}", window, videoEngine);
    }

    @Override
    public void destroy() {
        getWindow().destroy();
    }

    public Window getWindow() {
        return window.get();
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
        for(Resource resource : Engine.getInstance().getResourceManager().snapLoadedResources())
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

    public boolean getVSync() {
        return vSync.get();
    }

    public void setVSync(boolean vSync) {
        this.vSync.set(vSync);
    }

    @Override
    protected void onStart() {
        Window.makeCurrent(getWindow());
        videoEngine.start();
    }

    @Override
    protected void onLoop() {
        videoEngine.update(getWindow().getFramebufferSize(), vSync.get());
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
        return ((VideoRecipe)recipe).resolve(videoEngine);
    }

    private final AtomicReference<Window> window = new AtomicReference<>();
    private VideoEngine videoEngine;

    private final AtomicReference<VideoAPI> api = new AtomicReference<>();
    private final AtomicBoolean vSync = new AtomicBoolean();
}
