package com.rayferric.comet.scenegraph.node.camera;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.util.AtomicFloat;

public class PerspectiveCamera extends Camera {
    public PerspectiveCamera(float near, float far, float fov) {
        super(near, far);
        this.fov = new AtomicFloat(fov);
    }

    @Override
    public Matrix4f getProjection(float ratio) {
        return Matrix4f.perspective(getFov(), ratio, getNear(), getFar());
    }

    public float getFov() {
        return fov.get();
    }

    public void setFov(float fov) {
        this.fov.set(fov);
    }

    private final AtomicFloat fov;
}
