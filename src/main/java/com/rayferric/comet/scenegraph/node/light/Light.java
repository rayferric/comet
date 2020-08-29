package com.rayferric.comet.scenegraph.node.light;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.Node;

import java.util.concurrent.atomic.AtomicReference;

public abstract class Light extends Node {
    public Vector3f getEnergy() {
        return energy.get();
    }

    public void setEnergy(Vector3f energy) {
        this.energy.set(energy);
    }

    protected Light(Vector3f energy) {
        setEnergy(energy);
    }

    private final AtomicReference<Vector3f> energy = new AtomicReference<>();
}
