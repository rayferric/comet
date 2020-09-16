package com.rayferric.comet.core.scenegraph.node.light;

import com.rayferric.comet.core.engine.LayerIndex;
import com.rayferric.comet.core.math.Vector3f;

public class PointLight extends Light {
    public PointLight(Vector3f energy) {
        super(energy);
        setName("Point Light");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }
}
