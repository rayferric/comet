package com.rayferric.comet.core.physics.bt.shape;

import com.bulletphysics.collision.shapes.SphereShape;

public class BTSphereCollisionShape extends BTCollisionShape {
    public BTSphereCollisionShape(float radius) {
        super(new SphereShape(radius));
    }
}
