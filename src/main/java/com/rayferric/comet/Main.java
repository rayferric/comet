package com.rayferric.comet;

import com.rayferric.comet.video.display.GLFW;
import com.rayferric.comet.video.display.Window;

import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        GLFW.init();

        Engine engine = new Engine(4);
        ThreadPoolExecutor threadPool = engine.getThreadPool();
        Window window = engine.getWindow();

        while(!window.shouldClose()) {
            Window.pollEvents();

            window.swapBuffers();
        }

        window.close();

        GLFW.terminate();
    }
}
