package com.rayferric.comet.physics;

import com.rayferric.comet.physics.bt.BTPhysicsEngine;
import com.rayferric.comet.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

public class PhysicsServer extends Server {
    @Override
    public void destroy() {}

    @Override
    protected void onStart() {
        physicsEngine = new BTPhysicsEngine();
    }

    @Override
    protected void onLoop() {
        synchronized(initializedNotifier) {
            initializedNotifier.notifyAll();
        }

        physicsEngine.step();
    }

    @Override
    protected void onStop() {
        physicsEngine.destroy();
    }

    @Override
    protected ServerResource resourceFromRecipe(ServerRecipe recipe) {
        return ((PhysicsRecipe)recipe).resolve(physicsEngine);
    }

    private PhysicsEngine physicsEngine;
    private final Object initializedNotifier = new Object();
}
