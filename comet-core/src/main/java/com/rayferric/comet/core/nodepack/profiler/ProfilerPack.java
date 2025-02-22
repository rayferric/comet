package com.rayferric.comet.core.nodepack.profiler;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.math.Transform;
import com.rayferric.comet.core.math.Vector4f;
import com.rayferric.comet.core.nodepack.NodePack;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.node.model.Graph;
import com.rayferric.comet.core.scenegraph.node.model.Label;
import com.rayferric.comet.core.scenegraph.node.model.Sprite;
import com.rayferric.comet.core.scenegraph.resource.font.Font;
import com.rayferric.comet.core.text.HorizontalAlignment;

public class ProfilerPack implements NodePack {
    public static ProfilerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        Node root = new ProfilerNode();

        Font font = new Font(true, "fonts/share-tech-mono-bold.fnt");

        { // FPS Label
            Label label = new Label();
            label.setName("FPS Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            root.addChild(label);
        }
        { // Frame Time Label
            Label label = new Label();
            label.setName("Frame Time Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -1, 0);
            root.addChild(label);
        }
        { // CPU + GPU Times Head Label
            Label label = new Label();
            label.setText("(ms) min avg max");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -3, 0);
            root.addChild(label);
        }
        { // CPU Time Label
            Label label = new Label();
            label.setName("CPU Time Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -4, 0);
            root.addChild(label);
        }
        { // GPU Time Label
            Label label = new Label();
            label.setName("GPU Time Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -5, 0);
            root.addChild(label);
        }
        { // Heap Memory Label
            Label label = new Label();
            label.setName("Heap Memory Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -7, 0);
            root.addChild(label);
        }
        { // Video Memory Label
            Label label = new Label();
            label.setName("Video Memory Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -8, 0);
            root.addChild(label);
        }
        { // Vertices Label
            Label label = new Label();
            label.setName("Vertices Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -10, 0);
            root.addChild(label);
        }
        { // Triangles Label
            Label label = new Label();
            label.setName("Triangles Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -11, 0);
            root.addChild(label);
        }
        { // Java Version Label
            Label label = new Label();
            label.setFont(font);
            label.setText("Java " + Runtime.version());
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -13, 0);
            root.addChild(label);
        }
        { // Device Model Label
            Label label = new Label();
            label.setText(Engine.getInstance().getVideoServer().getVideoInfo().getDeviceModel());
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -14, 0);
            root.addChild(label);
        }
        { // Video API Version Label
            Label label = new Label();
            label.setName("Video API Version Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -15, 0);
            root.addChild(label);
        }
        { // Shader Version Label
            Label label = new Label();
            label.setName("Shader Version Label");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(0, -16, 0);
            root.addChild(label);
        }
        { // CPU Graph Label
            Label label = new Label();
            label.setText("CPU: ");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(-8, -19, 0);
            root.addChild(label);
        }
        { // CPU Graph Background
            Sprite sprite = new Sprite();
            sprite.getMaterial().setColor(new Vector4f(0, 0, 0, 0.5F));
            sprite.getMaterial().setTranslucent(true);
            Transform t = sprite.getTransform();
            t.setTranslation(-4, -19, -1);
            t.setScale(8, 2, 1);
            root.addChild(sprite);
        }
        { // CPU Graph
            Graph graph = new Graph();
            graph.setName("CPU Graph");
            Transform t = graph.getTransform();
            t.setTranslation(-8, -20, 0);
            t.setScale(8, 2, 1);
            root.addChild(graph);
        }
        { // GPU Graph Label
            Label label = new Label();
            label.setText("GPU: ");
            label.setFont(font);
            label.setHAlign(HorizontalAlignment.RIGHT);
            label.getMaterial().setSoftness(0.2F);
            label.getTransform().setTranslation(-8, -22, 0);
            root.addChild(label);
        }
        { // GPU Graph Background
            Sprite sprite = new Sprite();
            sprite.getMaterial().setColor(new Vector4f(0, 0, 0, 0.5F));
            sprite.getMaterial().setTranslucent(true);
            Transform t = sprite.getTransform();
            t.setTranslation(-4, -22, -1);
            t.setScale(8, 2, 1);
            root.addChild(sprite);
        }
        { // GPU Graph
            Graph graph = new Graph();
            graph.setName("GPU Graph");
            Transform t = graph.getTransform();
            t.setTranslation(-8, -23, 0);
            t.setScale(8, 2, 1);
            root.addChild(graph);
        }

        root.initAll();
        return root;
    }

    private static final ProfilerPack INSTANCE = new ProfilerPack();

    private ProfilerPack() {}
}
