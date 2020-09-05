package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.server.ServerResource;

public class BTPhysicsWorld implements ServerResource {
    public BTPhysicsWorld() {
        collisionCfg = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionCfg);
        broadPhase = new DbvtBroadphase();
        solver = new SequentialImpulseConstraintSolver();
        world = new DiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionCfg);
        world.setGravity(new javax.vecmath.Vector3f(0, 0, 0));
    }

    @Override
    public void destroy() {
        world.destroy();
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
        world.setGravity(new javax.vecmath.Vector3f(gravity.getX(), gravity.getY(), gravity.getZ()));
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    DefaultCollisionConfiguration collisionCfg;
    CollisionDispatcher dispatcher;
    DbvtBroadphase broadPhase;
    SequentialImpulseConstraintSolver solver;
    DiscreteDynamicsWorld world;
    Vector3f gravity = Vector3f.ZERO;
}
