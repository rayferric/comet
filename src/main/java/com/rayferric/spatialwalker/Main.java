package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.*;
import com.rayferric.comet.nodepack.profiler.ProfilerPack;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.node.model.Sprite;
import com.rayferric.comet.scenegraph.resource.scene.GLTFScene;
import com.rayferric.comet.scenegraph.resource.scene.Scene;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.node.Rotor;
import com.rayferric.spatialwalker.node.SpectatorCamera;

public class Main {
    public static void main(String[] args) {
        Engine engine = Engine.getInstance();

        EngineInfo info = new EngineInfo();
        info.setWindowTitle("Spatial Walker");
        info.setWindowSize(new Vector2i(1280, 720));

        info.setVideoApi(VideoAPI.OPENGL);
        info.setVSync(true);
        info.setTextureFilter(TextureFilter.TRILINEAR);
        info.setTextureAnisotropy(8);

        info.setLoaderThreads(4);
        info.setJobThreads(4);

        info.setLayerCount(2);

        try {
            engine.start(info);

            Layer mainLayer = engine.getLayerManager().getLayers()[0];
            Layer overlayLayer = engine.getLayerManager().getLayers()[1];

            {
                Camera camera = new SpectatorCamera(0.1F, 1000, 70);
                Transform t = new Transform();
                t.setTranslation(0, 0, 4);
                camera.setTransform(t);
                mainLayer.getRoot().addChild(camera);
                mainLayer.setCamera(camera);
            }
            {
                Camera camera = new OrthographicCamera(-1, 1, 2);
                overlayLayer.setCamera(camera);
            }

            Rotor rotor = new Rotor();
            mainLayer.getRoot().addChild(rotor);

            Sprite sprite = new Sprite();
            sprite.setTexture(new ImageTexture(false, "data/textures/texture.png", true));
            {
                Transform t = new Transform();
                t.setTranslation(0, 0, -1);
                sprite.setTransform(t);
            }
            rotor.addChild(sprite);

            mainLayer.getRoot().initAll();

            {
                Node profiler = ProfilerPack.getInstance().instantiate();
                overlayLayer.getRoot().addChild(profiler);
            }

            Scene scene = new GLTFScene("data/local/damaged-helmet/DamagedHelmet.gltf");
            var ref = new Object() {
                public boolean sceneInstantiated = false;
            };

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

                if(scene.isLoaded() && !ref.sceneInstantiated) {
                    ref.sceneInstantiated = true;
                    rotor.addChild(scene.instantiate());
                    scene.unload();
                }

                // TODO Debug only, not to waste electricity (this destroys GC throughput):
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            });

            engine.stop();
        } catch(RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
