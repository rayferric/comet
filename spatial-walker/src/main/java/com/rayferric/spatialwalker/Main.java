package com.rayferric.spatialwalker;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.engine.EngineInfo;
import com.rayferric.comet.core.engine.Layer;
import com.rayferric.comet.core.input.InputKey;
import com.rayferric.comet.core.math.*;
import com.rayferric.comet.core.nodepack.profiler.ProfilerPack;
import com.rayferric.comet.core.scenegraph.common.Collider;
import com.rayferric.comet.core.scenegraph.common.Surface;
import com.rayferric.comet.core.scenegraph.common.material.GLTFMaterial;
import com.rayferric.comet.core.scenegraph.node.*;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.core.scenegraph.node.camera.Camera;
import com.rayferric.comet.core.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.core.scenegraph.node.model.Model;
import com.rayferric.comet.core.scenegraph.node.model.Sprite;
import com.rayferric.comet.core.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.core.scenegraph.resource.physics.shape.BoxCollisionShape;
import com.rayferric.comet.core.scenegraph.resource.scene.GLTFScene;
import com.rayferric.comet.core.scenegraph.resource.scene.Scene;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.BoxMesh;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.core.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.core.shape.ShapeLoader;
import com.rayferric.comet.core.video.api.VideoAPI;
import com.rayferric.comet.core.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.pack.Rotor;
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

            {
                PhysicsBody physicsBody = new PhysicsBody();
                physicsBody.addCollider(new Collider(new BoxCollisionShape(new Vector3f(1000, 1, 1000)), Matrix4f.IDENTITY));
                physicsBody.setMass(1000);
                physicsBody.setKinematic(true);
                Model model = new Model();
                model.addSurface(new Surface(new BoxMesh(new Vector3f(32, 1, 32), false), new GLTFMaterial()));
                physicsBody.addChild(model);
                physicsBody.getTransform().setTranslation(0, -0.5F, 0);
                mainLayer.getRoot().addChild(physicsBody);
            }
            {
                PhysicsBody physicsBody = new PhysicsBody();
                physicsBody.addCollider(new Collider(new BoxCollisionShape(new Vector3f(32, 1, 32)), Matrix4f.IDENTITY));
                physicsBody.setMass(0);
                Model model = new Model();
                model.addSurface(new Surface(new BoxMesh(new Vector3f(32, 1, 32), false), new GLTFMaterial()));
                physicsBody.addChild(model);
                physicsBody.getTransform().setTranslation(30, 3, 0);
                physicsBody.getTransform().setRotation(0, 0, 15);
                mainLayer.getRoot().addChild(physicsBody);
            }
            {
                PhysicsBody physicsBody = new PhysicsBody();
                physicsBody.addCollider(new Collider(new BoxCollisionShape(new Vector3f(4, 1, 2)), Matrix4f.IDENTITY));
                physicsBody.setMass(0);
                Model model = new Model();
                model.addSurface(new Surface(new BoxMesh(new Vector3f(4, 1, 2), false), new GLTFMaterial()));
                physicsBody.addChild(model);
                physicsBody.getTransform().setTranslation(0, 0, 14);
                physicsBody.getTransform().setRotation(0, 0, -30);
                mainLayer.getRoot().addChild(physicsBody);
            }
            {
                PhysicsBody physicsBody = new PhysicsBody();
                physicsBody.addCollider(new Collider(new BoxCollisionShape(new Vector3f(32, 2, 32)), Matrix4f.IDENTITY));
                physicsBody.setMass(0);
                Model model = new Model();
                model.addSurface(new Surface(new BoxMesh(new Vector3f(32, 2, 32), false), new GLTFMaterial()));
                physicsBody.addChild(model);
                physicsBody.getTransform().setTranslation(0, 14, -12);
                physicsBody.getTransform().setRotation(-90, 0, 0);
                mainLayer.getRoot().addChild(physicsBody);
            }
            {
                Collider collider = new Collider(new BoxCollisionShape(new Vector3f(1)), Matrix4f.IDENTITY);
                Mesh mesh = new BoxMesh(new Vector3f(1), false);
                GLTFMaterial mat = new GLTFMaterial();
                mat.setColorMap(new ImageTexture(false, "data/textures/crate.jpg", true));

                int numX = 4, numY = 3, numZ = 4;

                for(int i = -numX; i <= numX; i++) {
                    for(int j = -numX; j <= numZ; j++) {
                        for(int k = 0; k < numY; k++) {
                            PhysicsBody physicsBody = new PhysicsBody();
                            physicsBody.addCollider(collider);
                            physicsBody.setMass(50);
                            physicsBody.setLinearDrag(0.5F);
                            Model model = new Model();
                            model.addSurface(new Surface(mesh, mat));
                            physicsBody.addChild(model);
                            physicsBody.getTransform().setTranslation(i * 1.05F, 0.5F + k * 1.05F, j * 1.05F);
                            mainLayer.getRoot().addChild(physicsBody);
                        }
                    }
                }
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
                player.getTransform().setTranslation(0, 0, 10);
                mainLayer.getRoot().addChild(player);
            }

//            Scene scene1 = new GLTFScene("data/local/goldmine.glb");
//            Scene scene2 = new GLTFScene("data/local/flight-helmet/FlightHelmet.gltf");

//            var ref = new Object() {
//                public boolean scene1Instantiated = false;
////                public boolean scene2Instantiated = false;
////                public float timeCounter = 0;
//            };

            // ShapeLoader.load(false, "data/local/goldmine.col");

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

//                if(scene1.isLoaded() && !ref.scene1Instantiated) {
//                    ref.scene1Instantiated = true;
//                    Node modelRoot = scene1.instantiate();
//                    if(modelRoot instanceof Model)
//                        ((Model)modelRoot).getSurface(0).getMaterial().setCulling(false);
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
                    audioPlayer.setPlaying(!audioPlayer.isPlaying());
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
