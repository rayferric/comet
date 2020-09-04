package com.rayferric.comet.scenegraph.resource.physics.shape;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.physics.recipe.shape.BoxCollisionShapeRecipe;

public class BoxCollisionShape extends CollisionShape {
    public BoxCollisionShape(Vector3f extents) {
        this.extents = extents;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new BoxCollisionShapeRecipe(extents)));
        finishLoading();

        return true;
    }

    private final Vector3f extents;
}
