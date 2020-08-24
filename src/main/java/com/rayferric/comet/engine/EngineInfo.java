package com.rayferric.comet.engine;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;

/**
 * Encapsulates engine configuration.<br>
 * • Provides defaults on all fields, those values that are optimal for a common system.<br>
 * • Is not thread-safe.
 */
public class EngineInfo {
    // <editor-fold desc="Getters and Setters">

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public Vector2i getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Vector2i windowSize) {
        this.windowSize = windowSize;
    }

    public VideoAPI getVideoApi() {
        return videoApi;
    }

    public void setVideoApi(VideoAPI videoApi) {
        this.videoApi = videoApi;
    }

    public boolean hasVSync() {
        return vSync;
    }

    public void setVSync(boolean vSyncEnabled) {
        this.vSync = vSyncEnabled;
    }

    public int getLoaderThreads() {
        return loaderThreads;
    }

    public void setLoaderThreads(int loaderThreads) {
        this.loaderThreads = loaderThreads;
    }

    public int getJobThreads() {
        return jobThreads;
    }

    public void setJobThreads(int jobThreads) {
        this.jobThreads = jobThreads;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    public TextureFilter getTextureFilter() {
        return textureFilter;
    }

    public void setTextureFilter(TextureFilter textureFilter) {
        this.textureFilter = textureFilter;
    }

    public float getTextureAnisotropy() {
        return textureAnisotropy;
    }

    public void setTextureAnisotropy(float textureAnisotropy) {
        this.textureAnisotropy = textureAnisotropy;
    }

    // </editor-fold>

    private String windowTitle = "Engine";
    private Vector2i windowSize = new Vector2i(1280, 720);

    private VideoAPI videoApi = VideoAPI.OPENGL;
    private boolean vSync = true;
    private TextureFilter textureFilter = TextureFilter.TRILINEAR;
    private float textureAnisotropy = 1;

    private int loaderThreads = 4;
    private int jobThreads = 4;

    private int layerCount = 1;
}
