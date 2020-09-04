package com.rayferric.comet.physics.recipe.shape;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.server.ServerResource;

public class BoxCollisionShapeRecipe extends PhysicsRecipe {
    public BoxCollisionShapeRecipe(Vector3f extents) {
        super(null);
        this.extents = extents;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createBoxCollisionShape(extents);
    }

    private final Vector3f extents;
}
