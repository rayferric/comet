package com.rayferric.comet.scenegraph.node.light;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.util.AtomicFloat;

public class SpotLight extends PointLight {
    public SpotLight(Vector3f energy, float fov) {
        super(energy);
        this.fov.set(fov);
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public float getFov() {
        return fov.get();
    }

    public void setFov(float fov) {
        this.fov.set(fov);
    }

    private final AtomicFloat fov = new AtomicFloat();
}
