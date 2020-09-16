package com.rayferric.comet.core.physics.recipe.shape;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.core.server.ServerResource;

public class CylinderCollisionShapeRecipe extends PhysicsRecipe {
    public CylinderCollisionShapeRecipe(float radius, float height) {
        super(null);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createCylinderCollisionShape(radius, height);
    }

    private final float radius, height;
}

