package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.nodepack.NodePack;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;

public class PlayerPack implements NodePack {
    public static PlayerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        PlayerNode root = new PlayerNode();

        {
            PerspectiveCamera camera = new PerspectiveCamera(0.01F, 1000F, 70F);
            camera.setName("Camera");
            camera.getTransform().setTranslation(0, 1.8F, 0);
            Engine.getInstance().getLayerManager().getLayers()[0].setCamera(camera);
            root.addChild(camera);
        }

        root.initAll();
        return root;
    }

    private static final PlayerPack INSTANCE = new PlayerPack();

    private PlayerPack() {}
}
