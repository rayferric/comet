package com.rayferric.comet.core.physics.recipe;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.server.ServerResource;

public class PhysicsWorldRecipe extends PhysicsRecipe {
    public PhysicsWorldRecipe() {
        super(null);
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createPhysicsWorld();
    }
}
