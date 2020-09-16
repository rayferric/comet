package com.rayferric.comet.core.physics.bt.shape;

import com.bulletphysics.collision.shapes.BoxShape;
import com.rayferric.comet.core.math.Vector3f;

public class BTBoxCollisionShape extends BTCollisionShape {
    public BTBoxCollisionShape(Vector3f size) {
        super(new BoxShape(new javax.vecmath.Vector3f(size.getX() * 0.5F, size.getY() * 0.5F, size.getZ() * 0.5F)));
    }
}
