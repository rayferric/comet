package com.rayferric.comet.physics.recipe;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
import com.rayferric.comet.server.ServerResource;

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
