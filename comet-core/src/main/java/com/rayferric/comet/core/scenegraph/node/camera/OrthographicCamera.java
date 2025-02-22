package com.rayferric.comet.core.scenegraph.node.camera;

import com.rayferric.comet.core.math.Matrix4f;
import com.rayferric.comet.core.util.AtomicFloat;

public class OrthographicCamera extends Camera {
    public OrthographicCamera(float near, float far, float size) {
        super(near, far);
        this.size = new AtomicFloat(size);
    }

    @Override
    public Matrix4f getProjection(float ratio) {
        float size = getSize();
        return Matrix4f.ortho(ratio * size, size, getNear(), getFar());
    }

    public float getSize() {
        return size.get();
    }

    public void setSize(float fov) {
        this.size.set(fov);
    }

    private final AtomicFloat size;
}
