package com.rayferric.spatialwalker.node;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.Node;

public class Rotor extends Node {
    public Rotor() {
        setName("Rotor");
    }

    @Override
    protected void init() {

    }

    @Override
    protected void update(double delta) {
        Vector3f r = getRotation();
        float ry = r.getY();
        ry += 45 * delta;
        if(ry >= 360) ry = 0;
        setRotation(new Vector3f(r.getX(), ry, r.getZ()));
    }
}
