package com.rayferric.comet;

import com.rayferric.comet.scenegraph.resource.video.ImageTexture;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();
        engine.start(4, Engine.VideoAPI.OPENGL, "Engine");
        engine.getWindow().setVisible(true);

        ImageTexture texture = new ImageTexture("texture.png");

        engine.run();

        engine.destroy();
        System.out.println("Terminated.");
    }
}
