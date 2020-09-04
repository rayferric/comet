package com.rayferric.comet.physics;

import com.rayferric.comet.physics.bt.BTPhysicsEngine;
import com.rayferric.comet.physics.recipe.PhysicsRecipe;
import com.rayferric.comet.server.Server;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.server.ServerResource;

public class PhysicsServer extends Server {
    @Override
    public void destroy() {}

    /**
     * Waits for the audio engine to initialize.<br>
     * • Returns when the audio engine starts processing.<br>
     * • The server must be running.<br>
     * • May be called from any thread.
     *
     * @throws IllegalStateException if the server is stopped
     */
    public void awaitInitialization() {
        synchronized(startStopLock) {
            if(!isRunning())
                throw new IllegalStateException("Attempted to wait for audio engine while the server was down.");
            synchronized(initializedNotifier) {
                try {
                    initializedNotifier.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

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
