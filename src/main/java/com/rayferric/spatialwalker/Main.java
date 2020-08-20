package com.rayferric.spatialwalker;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.common.TextureFilter;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        EngineInfo info = new EngineInfo();
        info.setVideoApi(VideoAPI.OPENGL);
        info.setTitle("Spatial Walker");
        info.setWindowSize(new Vector2i(640, 360));
        info.setLoaderThreads(4);
        info.setJobThreads(4);

        try {
            engine.start(info);

            ImageTexture texture = new ImageTexture("texture.png", TextureFilter.BILINEAR);
            Shader shader = new BinaryShader("shader.vert.spv", "shader.frag.spv");
            Shader shader2 = new SourceShader("shader.vert", "shader.frag");

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
