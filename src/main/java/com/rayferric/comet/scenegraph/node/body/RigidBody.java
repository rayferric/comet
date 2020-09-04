package com.rayferric.comet.scenegraph.node.body;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.physics.RigidBodyResource;
import com.rayferric.comet.util.AtomicFloat;

public class RigidBody extends Node {
    public RigidBody() {
        setName("Rigid Body");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public float getMass() {
        return mass.get();
    }

    public void setMass(float mass) {
        this.mass.set(mass);
    }

    public RigidBodyResource getResource() {
        return resource;
    }

    private final RigidBodyResource resource = new RigidBodyResource();
    private final AtomicFloat mass = new AtomicFloat(1);
}
