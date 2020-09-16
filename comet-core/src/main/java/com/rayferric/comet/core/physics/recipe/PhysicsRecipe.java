package com.rayferric.comet.core.physics.recipe;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.server.ServerRecipe;
import com.rayferric.comet.core.server.ServerResource;

public abstract class PhysicsRecipe extends ServerRecipe {
    public PhysicsRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(PhysicsEngine physicsEngine);
}
