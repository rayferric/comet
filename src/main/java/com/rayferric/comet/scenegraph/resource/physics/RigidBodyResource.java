package com.rayferric.comet.scenegraph.resource.physics;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.physics.recipe.RigidBodyRecipe;

public class RigidBodyResource extends PhysicsResource {
    public RigidBodyResource() {
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new RigidBodyRecipe()));
        finishLoading();

        return true;
    }
}
