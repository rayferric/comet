package com.rayferric.comet.physics.bt.shape;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.rayferric.comet.server.ServerResource;

public abstract class BTCollisionShape implements ServerResource {
    @Override
    public void destroy() {}

    public CollisionShape getShape() {
        return shape;
    }

    protected BTCollisionShape(CollisionShape shape) {
        this.shape = shape;
    }

    private final CollisionShape shape;
}
