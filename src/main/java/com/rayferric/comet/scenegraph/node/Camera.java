package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.util.AtomicFloat;

public abstract class Camera extends Node {
    public abstract Matrix4f getProjection(float ratio);

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
