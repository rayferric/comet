package com.rayferric.comet.scenegraph.node.light;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;

public class PointLight extends Light {
    public PointLight(Vector3f energy) {
        super(energy);
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }
}
