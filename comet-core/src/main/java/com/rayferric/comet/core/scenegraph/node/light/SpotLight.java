package com.rayferric.comet.core.scenegraph.node.light;

import com.rayferric.comet.core.engine.LayerIndex;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.util.AtomicFloat;

public class SpotLight extends PointLight {
    public SpotLight(Vector3f energy, float fov) {
        super(energy);
        setName("Spot Light");
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
