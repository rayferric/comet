package com.rayferric.comet.core.scenegraph.resource.physics.shape;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.physics.recipe.shape.SphereCollisionShapeRecipe;

public class SphereCollisionShape extends CollisionShape {
    public SphereCollisionShape(float radius) {
        this.radius = radius;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer()
                .scheduleResourceCreation(new SphereCollisionShapeRecipe(radius)));
        finishLoading();

        return true;
    }

    private final float radius;
}