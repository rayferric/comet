package com.rayferric.spatialwalker;

import com.rayferric.comet.Engine;
import com.rayferric.comet.EngineInfo;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.video.material.BasicMaterial;
import com.rayferric.comet.scenegraph.resource.video.material.Material;
import com.rayferric.comet.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.scenegraph.resource.video.mesh.PlaneMesh;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.EmptyTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.video.VideoAPI;
import com.rayferric.comet.video.common.WindowMode;
import com.rayferric.comet.video.common.texture.TextureFilter;
import com.rayferric.comet.video.common.texture.TextureFormat;

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

            Mesh mesh = new PlaneMesh(new Vector2f(2));

            Texture imageTexture = new ImageTexture(false, "data/texture.png", TextureFilter.TRILINEAR);

            BasicMaterial material = new BasicMaterial(new Vector3f(0.5F, 0, 0));
            material.setColorTex(imageTexture);

            Node node = new Model(mesh, material);

            engine.root = node;

            engine.getVideoServer().getWindow().setMode(WindowMode.FULLSCREEN);

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
