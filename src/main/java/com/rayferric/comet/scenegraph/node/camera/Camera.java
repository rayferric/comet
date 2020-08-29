package com.rayferric.comet.scenegraph.node.camera;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.util.AtomicFloat;

public abstract class Camera extends Node {
    public abstract Matrix4f getProjection(float ratio);

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public float getNear() {
        return near.get();
    }

    public void setNear(float near) {
        this.near.set(near);
    }

    public float getFar() {
        return far.get();
    }

    public void setFar(float far) {
        this.far.set(far);
    }

    protected final AtomicFloat near, far;

    protected Camera(float near, float far) {
        this.near = new AtomicFloat(near);
        this.far = new AtomicFloat(far);
    }
}
