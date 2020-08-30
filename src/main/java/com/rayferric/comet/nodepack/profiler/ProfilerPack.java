package com.rayferric.comet.nodepack.profiler;

import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.nodepack.NodePack;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.scenegraph.node.model.Sprite;

public class ProfilerPack implements NodePack {
    public static ProfilerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        Node root = new Node();
        {
            Sprite background = new Sprite();
            background.getMaterial().setColor(new Vector4f(0, 0, 0, 0.25));
            Transform transform = new Transform();
            transform.setScale(0.5F, 1, 1);
            background.setTransform(transform);
            root.addChild(background);
        }
        {
            Label fps = new Label();
            fps.getMaterial().setColor(new Vector4f(0, 0, 0, 0.25));
            Transform transform = new Transform();
            transform.setTranslation(0, 1, 0);
            transform.setScale(0.5F, 1, 1);
            fps.setTransform(transform);
            root.addChild(fps);
        }

        return root;
    }

    private static final ProfilerPack INSTANCE = new ProfilerPack();

    private ProfilerPack() {}
}
