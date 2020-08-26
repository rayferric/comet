package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.component.material.BasicMaterial;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.resource.scene.GLTFScene;
import com.rayferric.comet.scenegraph.resource.scene.Scene;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.PlaneGeometry;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.node.Rotor;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        EngineInfo info = new EngineInfo();
        info.setWindowTitle("Spatial Walker");
        info.setWindowSize(new Vector2i(640, 360));

        info.setVideoApi(VideoAPI.OPENGL);
        info.setVSync(true);
        info.setTextureFilter(TextureFilter.TRILINEAR);
        info.setTextureAnisotropy(8);

        info.setLoaderThreads(1);
        info.setJobThreads(4);

        info.setLayerCount(1);

        try {
            engine.start(info);

            Scene scene = new GLTFScene("data\\local\\sponza-gltf-pbr\\sponza.glb");
            while(!scene.isLoaded()) {
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Node[] nodes = scene.instantiate();

            System.out.println("Instantiated.");

            Geometry geometry = new PlaneGeometry(new Vector2f(1), false);

            Texture imageTexture = new ImageTexture(false, "data/textures/texture.png", true);

            BasicMaterial material = new BasicMaterial();
            material.setColor(new Vector3f(1));
            material.setColorTex(imageTexture);

            Layer mainLayer = engine.getLayerManager().getLayers()[0];

            Rotor rotor = new Rotor();
            mainLayer.getRoot().addChild(rotor);
            rotor.addChild(nodes[0]);
            nodes[0].setScale(new Vector3f(0.1F));
            nodes[0].setTranslation(new Vector3f(0, -10, 0));

            Model model = new Model(new Mesh[] { new Mesh(geometry, material) });
            model.setName("Model");
            rotor.addChild(model);

            Camera camera = new PerspectiveCamera(0.1F, 1000, 90);
            camera.setTranslation(new Vector3f(0, 0, 2));
            mainLayer.setCamera(camera);

            engine.getVideoServer().waitForVideoEngine();

            // engine.getVideoServer().getWindow().setFullscreen(true);

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

                try {
                    Thread.sleep(10);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                // System.gc();
            });

            engine.stop();
        } catch(RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
