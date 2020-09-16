package com.rayferric.comet.core.scenegraph.common;

import com.rayferric.comet.core.math.Matrix4f;
import com.rayferric.comet.core.scenegraph.resource.physics.shape.CollisionShape;

import java.util.concurrent.atomic.AtomicReference;

public class Collider {
    public Collider(CollisionShape shape, Matrix4f transform) {
        this.shape.set(shape);
        this.transform.set(transform);
    }

    public CollisionShape getShape() {
        return shape.get();
    }

    public void setShape(CollisionShape shape) {
        this.shape.set(shape);
    }

    public Matrix4f getTransform() {
        return transform.get();
    }

    public void setTransform(Matrix4f transform) {
        this.transform.set(transform);
    }

    private final AtomicReference<CollisionShape> shape = new AtomicReference<CollisionShape>();
    private final AtomicReference<Matrix4f> transform = new AtomicReference<Matrix4f>();
}
