package com.rayferric.comet.nodepack.profiler;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.nodepack.NodePack;
import com.rayferric.comet.scenegraph.component.material.BasicMaterial;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.model.Graph;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.scenegraph.node.model.Sprite;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.text.HorizontalAlignment;

public class ProfilerPack implements NodePack {
    public static ProfilerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        Node root = new ProfilerNode();

        Font font = new Font(false, "data/fonts/share-tech-mono-bold.fnt");

        {
            Label fpsLabel = new Label();
            fpsLabel.setName("FPS Label");
            fpsLabel.setFont(font);
            fpsLabel.setHAlign(HorizontalAlignment.RIGHT);
            fpsLabel.getMaterial().setColor(new Vector4f(1));
            root.addChild(fpsLabel);
        }
        {
            Label fpsLabel = new Label();
            fpsLabel.setName("Frame Time Label");
            fpsLabel.setFont(font);
            fpsLabel.setHAlign(HorizontalAlignment.RIGHT);
            fpsLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -1, 0);
            fpsLabel.setTransform(t);
            root.addChild(fpsLabel);
        }
        {
            Label videoTimeHeaderLabel = new Label();
            videoTimeHeaderLabel.setText("(ms) min avg max");
            videoTimeHeaderLabel.setFont(font);
            videoTimeHeaderLabel.setHAlign(HorizontalAlignment.RIGHT);
            videoTimeHeaderLabel.getMaterial().setColor(new Vector4f(1));
            videoTimeHeaderLabel.getMaterial().setSoftness(0.2F);
            Transform t = new Transform();
            t.setTranslation(0, -3, 0);
            videoTimeHeaderLabel.setTransform(t);
            root.addChild(videoTimeHeaderLabel);
        }
        {
            Label cpuTimeLabel = new Label();
            cpuTimeLabel.setName("CPU Time Label");
            cpuTimeLabel.setFont(font);
            cpuTimeLabel.setHAlign(HorizontalAlignment.RIGHT);
            cpuTimeLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -4, 0);
            cpuTimeLabel.setTransform(t);
            root.addChild(cpuTimeLabel);
        }
        {
            Label cpuTimeLabel = new Label();
            cpuTimeLabel.setName("GPU Time Label");
            cpuTimeLabel.setFont(font);
            cpuTimeLabel.setHAlign(HorizontalAlignment.RIGHT);
            cpuTimeLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -5, 0);
            cpuTimeLabel.setTransform(t);
            root.addChild(cpuTimeLabel);
        }
        {
            Label cheapMemoryLabel = new Label();
            cheapMemoryLabel.setName("Cheap Memory Label");
            cheapMemoryLabel.setFont(font);
            cheapMemoryLabel.setHAlign(HorizontalAlignment.RIGHT);
            cheapMemoryLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -7, 0);
            cheapMemoryLabel.setTransform(t);
            root.addChild(cheapMemoryLabel);
        }
        {
            Label videoMemoryLabel = new Label();
            videoMemoryLabel.setName("Video Memory Label");
            videoMemoryLabel.setFont(font);
            videoMemoryLabel.setHAlign(HorizontalAlignment.RIGHT);
            videoMemoryLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -8, 0);
            videoMemoryLabel.setTransform(t);
            root.addChild(videoMemoryLabel);
        }
        {
            Label javaVersionLabel = new Label();
            javaVersionLabel.setFont(font);
            javaVersionLabel.setText("Java " + Runtime.version());
            javaVersionLabel.setHAlign(HorizontalAlignment.RIGHT);
            javaVersionLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -10, 0);
            javaVersionLabel.setTransform(t);
            root.addChild(javaVersionLabel);
        }
        {
            Label gpuModelLabel = new Label();
            gpuModelLabel.setText(Engine.getInstance().getVideoServer().getVideoInfo().getDeviceModel());
            gpuModelLabel.setFont(font);
            gpuModelLabel.setHAlign(HorizontalAlignment.RIGHT);
            gpuModelLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -11, 0);
            gpuModelLabel.setTransform(t);
            root.addChild(gpuModelLabel);
        }
        {
            Label videoApiVersionLabel = new Label();
            videoApiVersionLabel.setName("Video API Version Label");
            videoApiVersionLabel.setFont(font);
            videoApiVersionLabel.setHAlign(HorizontalAlignment.RIGHT);
            videoApiVersionLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -12, 0);
            videoApiVersionLabel.setTransform(t);
            root.addChild(videoApiVersionLabel);
        }
        {
            Label shaderVersionLabel = new Label();
            shaderVersionLabel.setName("Shader Version Label");
            shaderVersionLabel.setFont(font);
            shaderVersionLabel.setHAlign(HorizontalAlignment.RIGHT);
            shaderVersionLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(0, -13, 0);
            shaderVersionLabel.setTransform(t);
            root.addChild(shaderVersionLabel);
        }
        {
            Label cpuGraphLabel = new Label();
            cpuGraphLabel.setText("CPU: ");
            cpuGraphLabel.setFont(font);
            cpuGraphLabel.setHAlign(HorizontalAlignment.RIGHT);
            cpuGraphLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(-8, -16, 0);
            cpuGraphLabel.setTransform(t);
            root.addChild(cpuGraphLabel);
        }
        {
            Sprite cpuGraphBg = new Sprite();
            cpuGraphBg.getMaterial().setColor(new Vector4f(0, 0, 0, 0.5F));
            cpuGraphBg.getMaterial().setTranslucent(true);
            Transform t = new Transform();
            t.setTranslation(-4, -16, -1);
            t.setScale(8, 2, 1);
            cpuGraphBg.setTransform(t);
            root.addChild(cpuGraphBg);
        }
        {
            Graph cpuGraph = new Graph();
            cpuGraph.setName("CPU Graph");
            Transform t = new Transform();
            t.setTranslation(-8, -17, 0);
            t.setScale(8, 2, 1);
            cpuGraph.setTransform(t);
            root.addChild(cpuGraph);
        }
        {
            Label gpuGraphLabel = new Label();
            gpuGraphLabel.setText("GPU: ");
            gpuGraphLabel.setFont(font);
            gpuGraphLabel.setHAlign(HorizontalAlignment.RIGHT);
            gpuGraphLabel.getMaterial().setColor(new Vector4f(1));
            Transform t = new Transform();
            t.setTranslation(-8, -19, 0);
            gpuGraphLabel.setTransform(t);
            root.addChild(gpuGraphLabel);
        }
        {
            Sprite gpuGraphBg = new Sprite();
            gpuGraphBg.getMaterial().setColor(new Vector4f(0, 0, 0, 0.5F));
            gpuGraphBg.getMaterial().setTranslucent(true);
            Transform t = new Transform();
            t.setTranslation(-4, -19, -1);
            t.setScale(8, 2, 1);
            gpuGraphBg.setTransform(t);
            root.addChild(gpuGraphBg);
        }
        {
            Graph gpuGraph = new Graph();
            gpuGraph.setName("GPU Graph");
            Transform t = new Transform();
            t.setTranslation(-8, -20, 0);
            t.setScale(8, 2, 1);
            gpuGraph.setTransform(t);
            root.addChild(gpuGraph);
        }

        root.initAll();
        return root;
    }

    private static final ProfilerPack INSTANCE = new ProfilerPack();

    private ProfilerPack() {}
}
