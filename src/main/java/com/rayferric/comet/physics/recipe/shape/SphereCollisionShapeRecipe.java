package com.rayferric.comet.physics.recipe.shape;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.server.ServerResource;

public class SphereCollisionShapeRecipe extends PhysicsRecipe {
    public SphereCollisionShapeRecipe(float radius) {
        super(null);
        this.radius = radius;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createSphereCollisionShape(radius);
    }

    private final float radius;
}
