package com.rayferric.comet.scenegraph.resource.physics;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.physics.recipe.AreaRecipe;
import com.rayferric.comet.scenegraph.node.physics.Area;

public class AreaResource extends PhysicsResource {
    public AreaResource(Area owner) {
        this.owner = owner;
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new AreaRecipe(owner)));
        finishLoading();

        return true;
    }

    private final Area owner;
}
