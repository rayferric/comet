package com.rayferric.comet.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.util.AtomicFloat;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.Window;
import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.api.gl.GLVideoEngine;
import com.rayferric.comet.video.api.gl.GLWindow;
import com.rayferric.comet.video.util.VideoInfo;
import com.rayferric.comet.video.util.texture.TextureFilter;

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

        if(api == VideoAPI.OPENGL)
            window.set(new GLWindow(info.getWindowTitle(), info.getWindowSize()));

        this.api.set(api);
        vSync.set(info.hasVSync());
        textureFilter.set(info.getTextureFilter());
        textureAnisotropy.set(info.getTextureAnisotropy());
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

    public VideoInfo getVideoInfo() {
        return videoInfo;
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
        if(api == VideoAPI.OPENGL)
            window.set(new GLWindow(oldWindow));
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

    public TextureFilter getTextureFilter() {
        return textureFilter.get();
    }

    public void setTextureFilter(TextureFilter filter) {
        textureFilter.set(filter);
        Engine.getInstance().getResourceManager().reloadResources(Texture.class);
    }

    public float getTextureAnisotropy() {
        return textureAnisotropy.get();
    }

    public void setTextureAnisotropy(float anisotropyLevel) {
        textureAnisotropy.set(anisotropyLevel);
        Engine.getInstance().getResourceManager().reloadResources(Texture.class);
    }

    /**
     * Waits for the video engine to initialize.<br>
     * • Returns when the video engine starts drawing.<br>
     * • The server must be running.<br>
     * • May be called from any thread.
     *
     * @throws IllegalStateException if the server is stopped
     */
    public void awaitInitialization() {
        synchronized(startStopLock) {
            if(!isRunning())
                throw new IllegalStateException("Attempted to wait for video engine while the server was down.");
            synchronized(initializedNotifier) {
                try {
                    initializedNotifier.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        Window.makeCurrent(getWindow());
        if(getApi() == VideoAPI.OPENGL)
            videoEngine = new GLVideoEngine(getWindow().getFramebufferSize(), vSync.get());
    }

    @Override
    protected void onLoop() {
        synchronized(initializedNotifier) {
            initializedNotifier.notifyAll();
        }
        videoEngine.update(getWindow().getFramebufferSize(), vSync.get());

        getWindow().swapBuffers();
        videoEngine.draw();
    }

    @Override
    protected void onStop() {
        videoEngine.destroy();
        Window.makeCurrent(null);
    }

    @Override
    protected ServerResource resourceFromRecipe(ServerRecipe recipe) {
        return ((VideoRecipe)recipe).resolve(videoEngine);
    }

    private final AtomicReference<Window> window = new AtomicReference<>();

    private final VideoInfo videoInfo = new VideoInfo();
    private final AtomicReference<VideoAPI> api = new AtomicReference<>();
    private final AtomicBoolean vSync = new AtomicBoolean();
    private final AtomicReference<TextureFilter> textureFilter = new AtomicReference<>();
    private final AtomicFloat textureAnisotropy = new AtomicFloat();

    private VideoEngine videoEngine;
    private final Object initializedNotifier = new Object();
}
