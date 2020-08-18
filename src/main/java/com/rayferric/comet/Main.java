package com.rayferric.comet;

import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.video.display.GLFW;
import com.rayferric.comet.video.display.Window;

public class Main {
    public static void main(String[] args) {
        GLFW.init();

        Engine engine = new Engine(4, Engine.VideoAPI.OPENGL, "Engine");
        engine.getWindow().setVisible(true);

        Texture texture = new Texture(engine, "texture.png");

        while(engine.getWindow().isOpen()) {
            Window.pollEvents();

            if(!engine.getWindow().hasFocus()) engine.getWindow().focus();

            if(engine.getWindow().shouldClose()) engine.stop(); // Example of use in a script

            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Terminated.");

        GLFW.terminate();
    }
}
