package com.rayferric.comet.physics.recipe;

import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.scenegraph.node.physics.Area;
import com.rayferric.comet.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.server.ServerResource;

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
