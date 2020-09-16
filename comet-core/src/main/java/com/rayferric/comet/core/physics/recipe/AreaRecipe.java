package com.rayferric.comet.core.physics.recipe;

import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.scenegraph.node.physics.Area;
import com.rayferric.comet.core.server.ServerResource;

public class AreaRecipe extends PhysicsRecipe {
    public AreaRecipe(Area owner) {
        super(null);
        this.owner = owner;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createArea(owner);
    }

    private final Area owner;
}
