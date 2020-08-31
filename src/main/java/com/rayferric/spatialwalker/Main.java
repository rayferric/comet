package com.rayferric.spatialwalker;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.EngineInfo;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.*;
import com.rayferric.comet.nodepack.profiler.ProfilerPack;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.component.material.BasicMaterial;
import com.rayferric.comet.scenegraph.node.*;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.camera.OrthographicCamera;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.node.model.Graph;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.node.model.Sprite;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.scenegraph.resource.scene.GLTFScene;
import com.rayferric.comet.scenegraph.resource.scene.Scene;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.GraphGeometry;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.texture.TextureFilter;
import com.rayferric.spatialwalker.node.Rotor;

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

        info.setLayerCount(2);

        try {
            engine.start(info);

            Layer mainLayer = engine.getLayerManager().getLayers()[0];
            Layer overlayLayer = engine.getLayerManager().getLayers()[1];

            {
                Camera camera = new PerspectiveCamera(0.1F, 1000, 70);
                Transform t = new Transform();
                t.setTranslation(0, 0, 4);
                camera.setTransform(t);
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

            Font font = new Font(false, "data/fonts/bernard-mt-condensed.fnt");
            Label label = new Label();
            label.setColor(new Vector4f(1, 1, 1, 1F));
            label.setAutoWrap(true);
            label.setWrapSize(5);
            label.setCharSpacing(0.85F);
            label.setLineSpacing(0.85F);
            label.setHAlign(HorizontalAlignment.CENTER);
            label.setVAlign(VerticalAlignment.CENTER);
            label.setFont(font);
            label.getMaterial().setCulling(false);
            {
                Transform t = new Transform();
                t.setScale(1F);
                t.setTranslation(0, 0.1F, 1F);
                label.setTransform(t);
            }
            rotor.addChild(label);

            {
                float[] values = new float[] { 0.3F, 0.5F, 0.1F, 0.7F, 0.3F,0.3F, 0.5F, 0.1F, 0.7F, 0.3F };
                Graph graph = new Graph();
                BasicMaterial material = new BasicMaterial();
                material.setCulling(false);
                material.setColor(new Vector4f(0, 1, 0, 1));
                graph.setMaterial(material);
                graph.setValues(values);
                Transform t = new Transform();
                t.setScale(5F);
                t.setTranslation(-1, 0, 0);
                graph.setTransform(t);
                rotor.addChild(graph);
            }

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

                // TODO Debug only, not to waste electricity (this destroys GC behavior and memory usage though):
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
