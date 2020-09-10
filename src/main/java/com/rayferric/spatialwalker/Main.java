package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.math.*;
import com.rayferric.comet.nodepack.profiler.ProfilerPack;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.common.Mesh;
import com.rayferric.comet.scenegraph.common.material.GLTFMaterial;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.node.model.Sprite;
import com.rayferric.comet.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.scenegraph.resource.physics.shape.BoxCollisionShape;
import com.rayferric.comet.scenegraph.resource.video.geometry.BoxGeometry;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.pack.Rotor;
import com.rayferric.spatialwalker.pack.SpectatorCamera;
import com.rayferric.spatialwalker.pack.player.PlayerPack;

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

            mainLayer.getPhysicsWorld().setGravity(Vector3f.DOWN.mul(9.81F));

            /*{
                Camera camera = new SpectatorCamera(0.1F, 1000, 80);
                camera.getTransform().setTranslation(0, 0, 4);
                mainLayer.getRoot().addChild(camera);
                mainLayer.setCamera(camera);
            }*/
            {
                Camera camera = new OrthographicCamera(-1, 1, 2);
                overlayLayer.setCamera(camera);
            }

            Rotor rotor = new Rotor();
            {
                mainLayer.getRoot().addChild(rotor);
            }
            PhysicsBody physicsBody = new PhysicsBody();
            {
                physicsBody.addCollider(new Collider(new BoxCollisionShape(new Vector3f(8, 1, 8)), Matrix4f.IDENTITY));
                physicsBody.setMass(0F);
                // physicsBody.setKinematic(true);
                Model model = new Model();
                model.addMesh(new Mesh(new BoxGeometry(new Vector3f(8, 1, 8), false), new GLTFMaterial()));
                physicsBody.addChild(model);
                physicsBody.getTransform().setTranslation(0, -0.5F, 0);
                mainLayer.getRoot().addChild(physicsBody);
            }

            {
                Sprite sprite = new Sprite();
                sprite.setTexture(new ImageTexture(false, "data/textures/texture.png", true));
                sprite.getMaterial().setColor(new Vector4f(1, 1, 1, 0.75F));
                sprite.getMaterial().setTranslucent(true);
                sprite.getMaterial().setCulling(false);
                sprite.getTransform().setScale(2F);
                rotor.addChild(sprite);
            }

            mainLayer.getRoot().initAll();

            {
                Node profiler = ProfilerPack.getInstance().instantiate();
                overlayLayer.getRoot().addChild(profiler);
            }

            AudioStream audioStream = new AudioStream(false, "data/audio/engine.ogg");
            AudioStream audioStream2 = new AudioStream(false, "data/audio/explosion.ogg");
            AudioPlayer audioPlayer = new AudioPlayer();
            {
                audioPlayer.setStream(audioStream);
                audioPlayer.setLooping(true);
                audioPlayer.setGain(1);
                audioPlayer.setAttenuationScale(1);
                audioPlayer.setMinDistance(0);
                mainLayer.getRoot().addChild(audioPlayer);
            }

            {
                Node player = PlayerPack.getInstance().instantiate();
                player.getTransform().setTranslation(0, 4, 0);
                mainLayer.getRoot().addChild(player);
            }

            //Scene scene1 = new GLTFScene("data/local/VC/VC.gltf");
            //Scene scene2 = new GLTFScene("data/local/flight-helmet/FlightHelmet.gltf");

            var ref = new Object() {
                public boolean scene1Instantiated = false;
                public boolean scene2Instantiated = false;
                public float timeCounter = 0;
            };

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

//                if(scene1.isLoaded() && !ref.scene1Instantiated) {
//                    ref.scene1Instantiated = true;
//                    Node modelRoot = scene1.instantiate();
//                    if(modelRoot instanceof Model)
//                        ((Model)modelRoot).getMesh(0).getMaterial().setCulling(false);
//                    mainLayer.getRoot().addChild(modelRoot);
//                    scene1.unload();
//                }
                if(Engine.getInstance().getInputManager().getKeyJustReleased(InputKey.KEYBOARD_L)) {
                    if(audioPlayer.getStream() == audioStream)
                        audioPlayer.setStream(audioStream2);
                    else
                        audioPlayer.setStream(audioStream);
                }
                if(Engine.getInstance().getInputManager().getKeyJustReleased(InputKey.KEYBOARD_U)) {
                    audioPlayer.setLooping(!audioPlayer.isLooping());
                }
                if(Engine.getInstance().getInputManager().getKeyJustReleased(InputKey.KEYBOARD_P)) {
                    if(!audioPlayer.isPlaying())audioPlayer.play();
                    else audioPlayer.reset();
                }

//                if(scene2.isLoaded() && !ref.scene2Instantiated) {
//                    ref.scene2Instantiated = true;
//                    mainLayer.getRoot().addChild(scene2.instantiate());
//                    scene2.unload();
//                }

                // TODO Debug only, not to waste electricity:
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });

            engine.stop();
        } catch(RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
