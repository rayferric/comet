package com.rayferric.comet;

import com.rayferric.comet.scenegraph.resource.video.Texture;
import com.rayferric.comet.video.Window;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();
        engine.start(4, Engine.VideoAPI.OPENGL, "Engine");
        engine.getWindow().setVisible(true);

        Texture texture = new Texture("texture.png");

        engine.run();

        engine.destroy();
        System.out.println("Terminated.");
    }
}
