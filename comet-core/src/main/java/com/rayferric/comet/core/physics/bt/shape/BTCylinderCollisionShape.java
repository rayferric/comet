package com.rayferric.comet.core.physics.bt.shape;

import com.bulletphysics.collision.shapes.CylinderShape;

public class BTCylinderCollisionShape extends BTCollisionShape {
    public BTCylinderCollisionShape(float radius, float height) {
        super(new CylinderShape(new javax.vecmath.Vector3f(radius, height * 0.5F, radius)));
    }
}
