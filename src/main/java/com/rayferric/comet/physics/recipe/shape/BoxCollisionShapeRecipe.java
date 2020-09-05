package com.rayferric.comet.physics.recipe.shape;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.server.ServerResource;

public class BoxCollisionShapeRecipe extends PhysicsRecipe {
    public BoxCollisionShapeRecipe(Vector3f size) {
        super(null);
        this.size = size;
    }

    @Override
    public ServerResource resolve(PhysicsEngine physicsEngine) {
        return physicsEngine.createBoxCollisionShape(size);
    }

    private final Vector3f size;
}
