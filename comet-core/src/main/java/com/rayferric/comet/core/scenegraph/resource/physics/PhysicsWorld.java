package com.rayferric.comet.core.scenegraph.resource.physics;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.physics.recipe.PhysicsWorldRecipe;

import java.util.concurrent.atomic.AtomicReference;

public class PhysicsWorld extends PhysicsResource {
    public PhysicsWorld() {
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getPhysicsServer().scheduleResourceCreation(new PhysicsWorldRecipe()));
        finishLoading();

        return true;
    }

    public Vector3f getGravity() {
        return gravity.get();
    }

    public void setGravity(Vector3f gravity) {
        this.gravity.set(gravity);
    }

    private final AtomicReference<Vector3f> gravity = new AtomicReference<>(Vector3f.ZERO);
}
