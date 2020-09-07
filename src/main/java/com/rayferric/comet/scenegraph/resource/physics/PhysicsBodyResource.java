package com.rayferric.comet.scenegraph.resource.physics;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.physics.recipe.PhysicsBodyRecipe;

public class PhysicsBodyResource extends PhysicsResource {
    public PhysicsBodyResource() {
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new PhysicsBodyRecipe()));
        finishLoading();

        return true;
    }
}
