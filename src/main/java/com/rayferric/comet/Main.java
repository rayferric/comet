package com.rayferric.comet;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.video.EmptyTexture;
import com.rayferric.comet.scenegraph.resource.video.ImageTexture;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();
        engine.start("Engine", Engine.VideoAPI.OPENGL, 4, 4);
        engine.getWindow().setVisible(true);

        for(int i = 0; i < 10; i++) {
            ImageTexture texture = new ImageTexture("texture.png", TextureFilter.BILINEAR);
            EmptyTexture emptyTexture =
                    new EmptyTexture(new Vector2i(100), TextureFormat.RGB32F, TextureFilter.BILINEAR);
        }
        engine.run();

        engine.destroy();
        System.out.println("Terminated.");
    }
}
