package com.rayferric.comet.scenegraph.resource.physics;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.physics.recipe.PhysicsBodyRecipe;
import com.rayferric.comet.scenegraph.node.PhysicsBody;

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

    private PhysicsBody owner;
}
