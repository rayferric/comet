package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.*;
import com.rayferric.comet.profiling.TimeAccumulator;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.util.VideoInfo;

public class Rotor extends Node {
    // Thread that instantiated the scene (or any other)
    public Rotor() {
        setName("Rotor");
    }

    // Main thread
    @Override
    protected void update(double delta) {
        super.update(delta);

        Transform transform = new Transform(getTransform());
        transform.rotate(0, (float)(45 * delta), 0);
        setTransform(transform);
    }

    // Planned: fixedUpdate(double) input(InputEvent) mainUpdate(double)
}
