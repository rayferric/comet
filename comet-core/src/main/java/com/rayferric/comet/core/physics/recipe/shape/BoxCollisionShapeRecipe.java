package com.rayferric.comet.core.physics.recipe.shape;

import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.physics.PhysicsEngine;
import com.rayferric.comet.core.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.core.server.ServerResource;

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
