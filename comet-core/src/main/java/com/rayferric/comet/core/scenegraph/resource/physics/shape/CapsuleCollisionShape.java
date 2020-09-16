package com.rayferric.comet.core.scenegraph.resource.physics.shape;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.physics.recipe.shape.CapsuleCollisionShapeRecipe;

public class CapsuleCollisionShape extends CollisionShape {
    public CapsuleCollisionShape(float radius, float height) {
        this.radius = radius;
        this.height = height;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(
                Engine.getInstance().getPhysicsServer()
                        .scheduleResourceCreation(new CapsuleCollisionShapeRecipe(radius, height)));
        finishLoading();

        return true;
    }

    private final float radius, height;
}
