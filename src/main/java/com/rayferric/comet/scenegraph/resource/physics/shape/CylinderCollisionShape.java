package com.rayferric.comet.scenegraph.resource.physics.shape;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.physics.recipe.shape.CapsuleCollisionShapeRecipe;
import com.rayferric.comet.physics.recipe.shape.CylinderCollisionShapeRecipe;

public class CylinderCollisionShape extends CollisionShape {
    public CylinderCollisionShape(float radius, float height) {
        this.radius = radius;
        this.height = height;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(
                Engine.getInstance().getPhysicsServer()
                        .scheduleResourceCreation(new CylinderCollisionShapeRecipe(radius, height)));
        finishLoading();

        return true;
    }

    private final float radius, height;
}
