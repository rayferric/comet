package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.server.ServerResource;

import java.util.ArrayList;
import java.util.List;

public class BTPhysicsWorld implements ServerResource {
    public BTPhysicsWorld() {
        DefaultCollisionConfiguration collisionCfg = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionCfg);
        DbvtBroadphase broadPhase = new DbvtBroadphase();
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionCfg);
        world.setGravity(new javax.vecmath.Vector3f(0, 0, 0));
    }

    @Override
    public void destroy() {
        for(BTPhysicsBody body : bodies)
            body.setWorld(null);
        world.destroy();
    }

    // Only to be used by BTPhysicsBody.setWorld(...).
    public void addBody(BTPhysicsBody body, short layer, short mask) {
        bodies.add(body);
        world.addRigidBody(body.getBtBody(), layer, mask);
    }

    // Only to be used by BTPhysicsBody.setWorld(...).
    public void removeBody(BTPhysicsBody body) {
        bodies.remove(body);
        world.removeRigidBody(body.getBtBody());
    }

    public Vector3f getGravity() {
        javax.vecmath.Vector3f gravity = new javax.vecmath.Vector3f();
        world.getGravity(gravity);
        return new Vector3f(gravity.x, gravity.y, gravity.z);
    }

    public void setGravity(Vector3f gravity) {
        world.setGravity(new javax.vecmath.Vector3f(gravity.getX(), gravity.getY(), gravity.getZ()));
    }

    public void step(float delta, int maxSubSteps) {
        world.stepSimulation(delta, maxSubSteps);
    }

    private final DiscreteDynamicsWorld world;
    private final List<BTPhysicsBody> bodies = new ArrayList<>();
}
