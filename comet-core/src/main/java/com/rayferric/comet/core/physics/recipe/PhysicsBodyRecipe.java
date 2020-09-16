package com.rayferric.comet.core.physics.recipe;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.core.server.ServerResource;

public class PhysicsBodyRecipe extends PhysicsRecipe {
    public PhysicsBodyRecipe(PhysicsBody owner) {
        super(null);
        this.owner = owner;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createPhysicsBody(owner);
    }

    private final PhysicsBody owner;
}
