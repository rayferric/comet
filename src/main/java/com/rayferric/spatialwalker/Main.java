package com.rayferric.spatialwalker;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.scenegraph.component.material.BasicMaterial;
import com.rayferric.comet.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.scenegraph.resource.video.mesh.PlaneMesh;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.WindowMode;
import com.rayferric.comet.video.util.texture.TextureFilter;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        EngineInfo info = new EngineInfo();
        info.setWindowTitle("Spatial Walker");
        info.setWindowSize(new Vector2i(640, 360));
        info.setWindowMode(WindowMode.FULLSCREEN);

        info.setVideoApi(VideoAPI.OPENGL);
        info.setVSync(true);

        info.setLoaderThreads(4);
        info.setJobThreads(4);

        try {
            engine.start(info);

            Mesh mesh = new PlaneMesh(new Vector2f(1));

            Texture imageTexture = new ImageTexture(false, "data/textures/texture.png", TextureFilter.TRILINEAR);

            BasicMaterial material = new BasicMaterial();
            material.setColor(new Vector3f(1, 0, 1));
            material.setColor2(new Vector3f(0, 1, 0));
            material.setColorTex(imageTexture);

            engine.root = new Model(mesh, material);

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
