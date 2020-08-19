package com.rayferric.comet;

import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.video.common.VideoAPI;
import com.rayferric.comet.video.common.texture.TextureFilter;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();
        engine.start("Engine", VideoAPI.OPENGL, 4, 4);
        engine.getVideoServer().getWindow().setVisible(true);

        ImageTexture texture = new ImageTexture("texture.png", TextureFilter.BILINEAR);

        engine.run();

        engine.destroy();
        System.out.println("Terminated.");
    }
}
