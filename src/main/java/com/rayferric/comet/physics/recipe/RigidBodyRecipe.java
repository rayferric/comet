package com.rayferric.comet.physics.recipe;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.server.ServerResource;

public class RigidBodyRecipe extends PhysicsRecipe {
    public RigidBodyRecipe() {
        super(null);
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createRigidBody();
    }
}
