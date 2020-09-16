package com.rayferric.comet.core.physics.recipe.shape;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.core.server.ServerResource;

public class CapsuleCollisionShapeRecipe extends PhysicsRecipe {
    public CapsuleCollisionShapeRecipe(float radius, float height) {
        super(null);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createCapsuleCollisionShape(radius, height);
    }

    private final float radius, height;
}

