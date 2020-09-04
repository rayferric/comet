package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.scenegraph.resource.physics.shape.CollisionShape;

import java.util.concurrent.atomic.AtomicReference;

public class Collider extends Node {
    public Collider() {
        setName("Collider");
    }

    public CollisionShape getShape() {
        return shape.get();
    }

    public void setShape(CollisionShape shape) {
        this.shape.set(shape);
    }

    private final AtomicReference<CollisionShape> shape = new AtomicReference<>(null);
}
