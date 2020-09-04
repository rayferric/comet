package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.math.*;
import com.rayferric.comet.nodepack.profiler.ProfilerPack;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.node.model.Sprite;
import com.rayferric.comet.scenegraph.resource.audio.AudioStream;
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
                Camera camera = new SpectatorCamera(0.1F, 1000, 80);
                camera.getTransform().setTranslation(0, 0, 4);
                mainLayer.getRoot().addChild(camera);
                mainLayer.setCamera(camera);
            }
            {
                Camera camera = new OrthographicCamera(-1, 1, 2);
                overlayLayer.setCamera(camera);
            }

            Rotor rotor = new Rotor();
            mainLayer.getRoot().addChild(rotor);

            {
                Sprite sprite = new Sprite();
                sprite.setTexture(new ImageTexture(false, "data/textures/texture.png", true));
                sprite.getTransform().setTranslation(0, 0, -1);
                rotor.addChild(sprite);
            }

            mainLayer.getRoot().initAll();

            {
                Node profiler = ProfilerPack.getInstance().instantiate();
                overlayLayer.getRoot().addChild(profiler);
            }

            AudioStream audioStream = new AudioStream(false, "data/audio/crystal-cave.ogg");
            AudioPlayer audioPlayer = new AudioPlayer();
            audioPlayer.setStream(audioStream);
            audioPlayer.setLooping(true);
            audioPlayer.setGain(1);
            audioPlayer.setAttenuationScale(0.1F);
            audioPlayer.play();
            mainLayer.getRoot().addChild(audioPlayer);

            Scene scene1 = new GLTFScene("data/local/sponza-gltf-pbr/sponza.glb");
            Scene scene2 = new GLTFScene("data/local/flight-helmet/FlightHelmet.gltf");
            var ref = new Object() {
                public boolean scene1Instantiated = false;
                public boolean scene2Instantiated = false;
            };

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

                if(scene1.isLoaded() && !ref.scene1Instantiated) {
                    ref.scene1Instantiated = true;
                    Node modelRoot = scene1.instantiate();
                    if(modelRoot instanceof Model)
                        ((Model)modelRoot).getMesh(0).getMaterial().setCulling(false);
                    mainLayer.getRoot().addChild(modelRoot);
                    scene1.unload();
                }
                // System.out.println(audioPlayer.isPlaying());
                if(Engine.getInstance().getInputManager().getKeyJustReleased(InputKey.KEYBOARD_L)) audioPlayer.setPaused(!audioPlayer.isPaused());

                if(scene2.isLoaded() && !ref.scene2Instantiated) {
                    ref.scene2Instantiated = true;
                    mainLayer.getRoot().addChild(scene2.instantiate());
                    scene2.unload();
                }

                // TODO Debug only, not to waste electricity:
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
