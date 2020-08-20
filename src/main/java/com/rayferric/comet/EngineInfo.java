package com.rayferric.comet;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.api.VideoAPI;

public class EngineInfo {
    // <editor-fold desc="Getters and Setters">

    public VideoAPI getVideoApi() {
        return videoApi;
    }

    public void setVideoApi(VideoAPI videoApi) {
        this.videoApi = videoApi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Vector2i getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Vector2i windowSize) {
        this.windowSize = windowSize;
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


    // </editor-fold>

    private VideoAPI videoApi = VideoAPI.OPENGL;
    private String title = "Engine";
    private Vector2i windowSize = new Vector2i(1280, 720);

    private int loaderThreads = 4;
    private int jobThreads = 4;
}
