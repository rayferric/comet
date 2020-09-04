package com.rayferric.comet.scenegraph.node.light;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;

public class DirectionalLight extends Light {
    public DirectionalLight(Vector3f energy) {
        super(energy);
        setName("Directional Light");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }
}
