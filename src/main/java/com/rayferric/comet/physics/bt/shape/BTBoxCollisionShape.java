package com.rayferric.comet.physics.bt.shape;

import com.bulletphysics.collision.shapes.BoxShape;
import com.rayferric.comet.math.Vector3f;

public class BTBoxCollisionShape extends BTCollisionShape {
    public BTBoxCollisionShape(Vector3f extents) {
        super(new BoxShape(new javax.vecmath.Vector3f(extents.getX(), extents.getY(), extents.getZ())));
    }
}
