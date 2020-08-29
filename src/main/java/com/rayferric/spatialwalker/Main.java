package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Transform;
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

        info.setLoaderThreads(4);
        info.setJobThreads(4);

        info.setLayerCount(1);

        try {
            engine.start(info);

            Scene scene = new GLTFScene("data\\local\\flight-helmet\\FlightHelmet.gltf");

            Geometry geometry = new PlaneGeometry(new Vector2f(1), false);

            Texture imageTexture = new ImageTexture(false, "data/textures/texture.png", true);

            BasicMaterial material = new BasicMaterial();
            material.setColorMap(imageTexture);

            Layer mainLayer = engine.getLayerManager().getLayers()[0];

            Rotor rotor = new Rotor();
            mainLayer.getRoot().addChild(rotor);

            Model model = new Model(new Mesh[] { new Mesh(geometry, material) });
            model.setName("Model");
            rotor.addChild(model);

            Camera camera = new PerspectiveCamera(0.1F, 1000, 90);
            {
                Transform transform = new Transform();
                transform.setTranslation(new Vector3f(0, 0, 2));
                camera.setTransform(transform);
            }
            mainLayer.setCamera(camera);

            mainLayer.getRoot().initAll();

            // engine.getVideoServer().getWindow().setFullscreen(true);

            var ref = new Object() {
                public boolean sceneInstantiated = false;
            };

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

                if(scene.isLoaded() && !ref.sceneInstantiated) {
                    ref.sceneInstantiated = true;
                    Node[] nodes = scene.instantiate();
                    scene.unload();
                    Node gltfModel = new Node();
                    gltfModel.setName("GLTF Model");
                    for(Node node : nodes)
                        gltfModel.addChild(node);
                    rotor.addChild(gltfModel);
                    {
                        Transform transform = new Transform();
                        transform.setScale(3);
                        transform.setTranslation(new Vector3f(0, -1, 0));
                        gltfModel.setTransform(transform);
                    }
                }

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
