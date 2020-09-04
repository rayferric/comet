package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.bt.shape.BTBoxCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTSphereCollisionShape;
import com.rayferric.comet.scenegraph.node.body.RigidBody;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.math.Vector3f;

public class BTPhysicsEngine extends PhysicsEngine {
    @Override
    public ServerResource createBoxCollisionShape(Vector3f extents) {
        return new BTBoxCollisionShape(extents);
    }

    @Override
    public ServerResource createSphereCollisionShape(float radius) {
        return new BTSphereCollisionShape(radius);
    }

    @Override
    public ServerResource createRigidBody() {
        return new BTRigidBody(world);
    }

    @Override
    protected void onStart() {
        collisionCfg = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionCfg);
        broadPhase = new DbvtBroadphase();
        solver = new SequentialImpulseConstraintSolver();
        world = new DiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionCfg);

        world.setGravity(new javax.vecmath.Vector3f(0,-1,0));

        deltaTimer = new Timer();

        deltaTimer.start();

        System.out.println("Bullet physics engine started.");
    }

    @Override
    protected void onStop() {
        world.destroy();

        System.out.println("Bullet physics engine stopped.");
    }

    @Override
    protected void onStep() {
        double deltaTime = deltaTimer.getElapsed();
        deltaTimer.reset();

        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            for(RigidBody rigidBody : layer.getIndex().getRigidBodies()) {
                BTRigidBody btRigidBody = (BTRigidBody)getRigidBodyOrNull(rigidBody.getResource());
                if(btRigidBody == null) continue;

                

                // Update Transform

                Matrix4f local = rigidBody.getTransform().getMatrix();
                Matrix4f global = rigidBody.getGlobalTransform();
                Matrix4f target = btRigidBody.getTransform();

                Matrix4f parentTransform = global.mul(local.inverse());
                Matrix4f newLocal = target.mul(parentTransform.inverse());

                rigidBody.getTransform().setMatrix(newLocal);
            }
        }

        world.stepSimulation((float)deltaTime);

        try {
            Thread.sleep(1);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    DefaultCollisionConfiguration collisionCfg;
    CollisionDispatcher dispatcher;
    DbvtBroadphase broadPhase;
    SequentialImpulseConstraintSolver solver;
    DiscreteDynamicsWorld world;

    private Timer deltaTimer;
}
