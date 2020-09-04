package com.rayferric.comet.physics.recipe;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

public abstract class PhysicsRecipe extends ServerRecipe {
    public PhysicsRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(PhysicsEngine physicsEngine);
}
