package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.nodepack.NodePack;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.common.Surface;
import com.rayferric.comet.scenegraph.common.material.GLTFMaterial;
import com.rayferric.comet.scenegraph.node.AudioPlayer;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.physics.Area;
import com.rayferric.comet.scenegraph.node.physics.RayCast;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.resource.audio.AudioStream;
import com.rayferric.comet.scenegraph.resource.physics.shape.CylinderCollisionShape;
import com.rayferric.comet.scenegraph.resource.video.mesh.BoxMesh;

public class PlayerPack implements NodePack {
    public static PlayerPack getInstance() {
        return INSTANCE;
    }

    @Override
    public Node instantiate() {
        PlayerNode root = new PlayerNode();
        Matrix4f colliderTransform = new Matrix4f(Matrix4f.IDENTITY);
        colliderTransform.setTranslation(0, 0.9F, 0);
        root.addCollider(new Collider(new CylinderCollisionShape(0.25F, 1.8F), colliderTransform));
        root.setMass(70F);
        root.setFriction(1F);
        root.setBounce(0);
        root.setLinearDrag(0.5F);
        root.setAngularFactor(0);

        {
            Node cameraPitch = new Node();
            cameraPitch.setName("Camera Pitch");
            cameraPitch.getTransform().setTranslation(0, 1.6F, 0);
            root.addChild(cameraPitch);

            PerspectiveCamera child = new PerspectiveCamera(0.01F, 1000F, 90F);
            child.setName("Camera");
            Engine.getInstance().getLayerManager().getLayers()[0].setCamera(child);
            cameraPitch.addChild(child);
        }
        {
            Area feetArea = new Area();
            feetArea.setName("Feet Area");
            feetArea.addCollider(new Collider(new CylinderCollisionShape(0.2F, 0.2F), Matrix4f.IDENTITY));
            root.addChild(feetArea);
        }
        {
            AudioPlayer child = new AudioPlayer();
            child.setName("Footsteps Audio Player");
            child.setStream(new AudioStream(false, "data/audio/footsteps.ogg"));
            child.setLooping(true);
            root.addChild(child);
        }
        {
            Model child = new Model();
            child.addSurface(new Surface(new BoxMesh(new Vector3f(0.5F, 1.8F, 0.5F), false), new GLTFMaterial()));
            child.getTransform().setTranslation(0, 0.9F, 0);
            ((GLTFMaterial)child.getSurface(0).getMaterial()).setColor(new Vector4f(1, 0.2, 0.2, 1));
            root.addChild(child);
        }

        root.initAll();
        return root;
    }

    private static final PlayerPack INSTANCE = new PlayerPack();

    private PlayerPack() {}
}
