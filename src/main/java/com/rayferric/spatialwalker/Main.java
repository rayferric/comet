package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.scenegraph.resource.scene.GLTFScene;
import com.rayferric.comet.scenegraph.resource.scene.Scene;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.node.Rotor;
import org.lwjgl.system.CallbackI;

import java.util.List;

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

            Scene scene = new GLTFScene("untitled.gltf");

            Layer mainLayer = engine.getLayerManager().getLayers()[0];

            Rotor rotor = new Rotor();
            mainLayer.getRoot().addChild(rotor);

            Sprite sprite = new Sprite(new ImageTexture(false, "data/textures/texture.png", true));
            {
                Transform t = new Transform();
                t.setTranslation(0, 0, -1);
                sprite.setTransform(t);
            }
            rotor.addChild(sprite);

            Camera camera = new PerspectiveCamera(0.1F, 1000, 70);
            {
                Transform t = new Transform();
                t.setTranslation(0, 0, 2);
                camera.setTransform(t);
            }
            mainLayer.setCamera(camera);

            mainLayer.getRoot().initAll();

            Font font = new Font(false, "data/fonts/bernard-mt-condensed.fnt");
            Label label = new Label();
            label.setColor(new Vector4f(1, 1, 1, 1F));
            label.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id diam sit amet quam cursus laoreet. Mauris vestibulum, arcu sed venenatis tempus, neque tortor vehicula odio, id consequat mauris leo ut erat. Nullam tempus mi quis sem aliquam porttitor. Maecenas mattis consequat semper. Praesent sit amet enim efficitur, vehicula nibh eget, pharetra purus. Sed venenatis efficitur quam vitae faucibus. Suspendisse lobortis fringilla consequat.");
            label.setAutoWrap(true);
            label.setWrapSize(20);
            label.setCharSpacing(0.85F);
            label.setLineSpacing(0.7F);
            label.setHAlign(HorizontalAlignment.CENTER);
            label.setVAlign(VerticalAlignment.CENTER);
            label.setFont(font);
            label.getMaterial().setCulling(false);
            {
                Transform t = new Transform();
                t.setScale(0.25F);
                label.setTransform(t);
            }
            rotor.addChild(label);

            var ref = new Object() {
                public boolean sceneInstantiated = false;
            };

            engine.run((delta) -> {
                if(engine.getVideoServer().getWindow().shouldClose())
                    engine.exit();

                if(scene.isLoaded() && !ref.sceneInstantiated) {
                    ref.sceneInstantiated = true;
                    List<Node> nodes = scene.instantiate();
                    scene.unload();
                    Node gltfModel = new Node();
                    gltfModel.setName("GLTF Model");
                    for(Node node : nodes)
                        gltfModel.addChild(node);
                    rotor.addChild(gltfModel);
                    {
                        Transform transform = new Transform();
                        transform.setScale(0.3F);
                        transform.setTranslation(new Vector3f(0, 0, 0));
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
