package com.rayferric.spatialwalker;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.EmptyTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.common.TextureFilter;
import com.rayferric.comet.video.common.TextureFormat;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        EngineInfo info = new EngineInfo();
        info.setVideoApi(VideoAPI.OPENGL);
        info.setTitle("Engine");
        info.setWindowSize(new Vector2i(640, 360));
        info.setLoaderThreads(4);
        info.setJobThreads(4);

        try {
            engine.start(info);

            for(int i=0; i < 100; i++) {
                Resource tmp = new EmptyTexture(new Vector2i(100, 100), TextureFormat.RGB8, TextureFilter.BILINEAR);
            }

            ImageTexture texture = new ImageTexture("texture.png", TextureFilter.BILINEAR);
            Shader shader = new Shader("shader.vert.spv", "shader.frag.spv");
            engine.run(() -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();
            });

            engine.stop();
        } catch(RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
