package com.rayferric.comet.core.scenegraph.resource.physics;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.physics.recipe.PhysicsBodyRecipe;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;

public class PhysicsBodyResource extends PhysicsResource {
    public PhysicsBodyResource(PhysicsBody owner) {
        this.owner = owner;
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new PhysicsBodyRecipe(owner)));
        finishLoading();

        return true;
    }

    private final PhysicsBody owner;
}
