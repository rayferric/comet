package com.rayferric.comet.physics.bt.shape;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.rayferric.comet.server.ServerResource;

public abstract class BTCollisionShape implements ServerResource {
    @Override
    public void destroy() {}

    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    protected BTCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    private final CollisionShape collisionShape;
}
