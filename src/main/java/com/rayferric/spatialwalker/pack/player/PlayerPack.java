package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.nodepack.NodePack;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.RayCast;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.resource.physics.shape.CapsuleCollisionShape;

public class PlayerPack implements NodePack {
    public static PlayerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        PlayerNode root = new PlayerNode();
        root.addCollider(new Collider(new CapsuleCollisionShape(0.5F, 0.8F), Matrix4f.IDENTITY));
        root.setMass(70F);
        root.setFriction(0.75F);
        root.setBounce(0);
        root.setLinearDrag(0.5F);
        root.setAngularFactor(new Vector3f(0));

        {
            PerspectiveCamera camera = new PerspectiveCamera(0.01F, 1000F, 70F);
            camera.setName("Camera");
            camera.getTransform().setTranslation(0, 1.6F, 0);
            Engine.getInstance().getLayerManager().getLayers()[0].setCamera(camera);
            root.addChild(camera);
        }
        {
            RayCast rayCast = new RayCast();
            rayCast.setName("Feet Ray");
            rayCast.getTransform().setTranslation(0, -0.8F, 0);
            rayCast.setVector(Vector3f.DOWN.mul(0.25F));
            rayCast.setEnabled(true);
            root.addChild(rayCast);
        }

        root.initAll();
        return root;
    }

    private static final PlayerPack INSTANCE = new PlayerPack();

    private PlayerPack() {}
}
