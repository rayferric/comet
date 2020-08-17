package com.rayferric.comet;

import com.rayferric.comet.resources.Resource;
import com.rayferric.comet.resources.Texture;
import com.rayferric.comet.server.VideoServer;
import com.rayferric.comet.util.AutoMap;
import com.rayferric.comet.video.display.Window;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Engine {
    public Engine(int numThreads) {
        threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);
        window.setVisible(true);
    }

    public void registerResource(Resource resource) {
        resources.put(resource);
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public Window getWindow() {
        return window;
    }

    public VideoServer getVideoServer() {
        return videoServer;
    }

    private final ThreadPoolExecutor threadPool;
    private final Window window = new Window("Engine", 1280, 720);
    private final AutoMap<Resource> resources = new AutoMap<>();
    private final VideoServer videoServer = new VideoServer(VideoServer.VideoAPI.OPENGL);
}
