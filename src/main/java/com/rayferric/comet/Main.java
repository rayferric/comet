package com.rayferric.comet;

import com.rayferric.comet.resources.Texture;
import com.rayferric.comet.video.display.GLFW;
import com.rayferric.comet.video.display.Window;

public class Main {
    public static void main(String[] args) {
        GLFW.init();

        Engine engine = new Engine(4, Engine.VideoAPI.OPENGL, "Engine");

        Texture texture = new Texture(engine, "texture.png");

        while(true) {
            Window.pollEvents();

            // engine.getVideoEngine().reloadResources();
        }

        engine.terminate();

        System.out.println("Terminated.");

        GLFW.terminate();
    }
}
