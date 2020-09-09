package com.rayferric.comet.physics.bt.shape;

import com.bulletphysics.collision.shapes.CapsuleShape;

public class BTCapsuleCollisionShape extends BTCollisionShape {
    public BTCapsuleCollisionShape(float radius, float height) {
        super(new CapsuleShape(radius, height));
    }
}
