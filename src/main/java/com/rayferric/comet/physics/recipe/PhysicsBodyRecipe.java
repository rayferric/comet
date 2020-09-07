package com.rayferric.comet.physics.recipe;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.server.ServerResource;

public class PhysicsBodyRecipe extends PhysicsRecipe {
    public PhysicsBodyRecipe() {
        super(null);
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createPhysicsBody();
    }
}
