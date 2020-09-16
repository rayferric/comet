package com.rayferric.comet.core.video;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.engine.EngineInfo;
import com.rayferric.comet.core.profiling.Profiler;
import com.rayferric.comet.core.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.core.server.Server;
import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.server.ServerRecipe;
import com.rayferric.comet.core.util.AtomicFloat;
import com.rayferric.comet.core.util.Timer;
import com.rayferric.comet.core.video.recipe.VideoRecipe;
import com.rayferric.comet.core.video.api.VideoAPI;
import com.rayferric.comet.core.video.api.gl.GLVideoEngine;
import com.rayferric.comet.core.video.api.gl.GLWindow;
import com.rayferric.comet.core.video.util.VideoInfo;
import com.rayferric.comet.core.video.util.texture.TextureFilter;

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

        frameTimer.start();
        cpuTimer.start();
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

        Window oldWindow = getWindow();
        if(api == VideoAPI.OPENGL)
            window.set(new GLWindow(oldWindow));
        oldWindow.destroy();

        Engine.getInstance().getResourceManager().reloadResources(VideoResource.class);

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

    @Override
    protected void onStart() {
        Window.makeCurrent(getWindow());
        if(getApi() == VideoAPI.OPENGL)
            videoEngine = new GLVideoEngine(getWindow().getFramebufferSize(), vSync.get());

        frameTimer.reset();
        cpuTimer.reset();
    }

    @Override
    protected void onLoop() {
        videoEngine.update(getWindow().getFramebufferSize(), vSync.get());

        Profiler profiler = Engine.getInstance().getProfiler();

        double frameDelta = frameTimer.getElapsed();
        frameTimer.reset();
        profiler.getFrameAccumulator().accumulate(frameDelta);

        double cpuDelta = cpuTimer.getElapsed();
        getWindow().swapBuffers();
        cpuTimer.reset();
        profiler.getCpuAccumulator().accumulate(cpuDelta);

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

    private VideoEngine videoEngine;

    private final AtomicReference<Window> window = new AtomicReference<>();
    private final VideoInfo videoInfo = new VideoInfo();
    private final AtomicReference<VideoAPI> api = new AtomicReference<>();
    private final AtomicBoolean vSync = new AtomicBoolean();
    private final AtomicReference<TextureFilter> textureFilter = new AtomicReference<>();
    private final AtomicFloat textureAnisotropy = new AtomicFloat();

    private final Timer frameTimer = new Timer(), cpuTimer = new Timer();
}
