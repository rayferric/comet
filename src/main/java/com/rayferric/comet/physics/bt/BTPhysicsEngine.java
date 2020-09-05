package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.bt.shape.BTBoxCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTSphereCollisionShape;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.body.RigidBody;
import com.rayferric.comet.scenegraph.resource.physics.PhysicsWorld;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BTPhysicsEngine extends PhysicsEngine {
    @Override
    public ServerResource createPhysicsWorld() {
        return new BTPhysicsWorld();
    }

    @Override
    public ServerResource createBoxCollisionShape(Vector3f size) {
        return new BTBoxCollisionShape(size);
    }

    @Override
    public ServerResource createSphereCollisionShape(float radius) {
        return new BTSphereCollisionShape(radius);
    }

    @Override
    public ServerResource createRigidBody() {
        return new BTRigidBody();
    }

    @Override
    protected void onStart() {
        deltaTimer = new Timer();

        deltaTimer.start();

        System.out.println("Bullet physics engine started.");
    }

    @Override
    protected void onStop() {
        System.out.println("Bullet physics engine stopped.");
    }

    @Override
    protected void onStep() {
        double deltaTime = deltaTimer.getElapsed();
        deltaTimer.reset();

        for(Layer layer : Engine.getInstance().getLayerManager().getLayers()) {
            PhysicsWorld world = layer.getPhysicsWorld();
            BTPhysicsWorld btWorld = (BTPhysicsWorld)getServerResourceOrNull(world);
            if(btWorld == null) continue;
            {
                Vector3f gravity = world.getGravity();
                if(!btWorld.getGravity().equals(gravity)) btWorld.setGravity(gravity);
            }
            DiscreteDynamicsWorld dynamicsWorld = btWorld.getWorld();

            List<RigidBody> rigidBodies = layer.getIndex().getRigidBodies();
            transformCache.clear();

            for(RigidBody rigidBody : rigidBodies) {
                BTRigidBody btRigidBody = (BTRigidBody)getServerResourceOrNull(rigidBody.getResource());
                if(btRigidBody == null) continue;

                // Ensure the right world:
                if(btRigidBody.getWorld() != dynamicsWorld) btRigidBody.setWorld(dynamicsWorld);

                // Update collider if needed:
                if(rigidBody.colliderNeedsUpdate()) {
                    CompoundShape compound = new CompoundShape();
                    boolean aborted = false;
                    for(Collider collider : rigidBody.snapColliders()) {
                        BTCollisionShape btShape = (BTCollisionShape)getServerResourceOrNull(collider.getShape());
                        if(btShape == null) {
                            aborted = true;
                            break;
                        }
                        Transform btTransform = new Transform();
                        btTransform.setFromOpenGLMatrix(collider.getTransform().toArray());
                        compound.addChildShape(btTransform, btShape.getShape());
                    }
                    if(!aborted) {
                        btRigidBody.setCollisionCompound(compound);
                        rigidBody.popColliderNeedsUpdate();
                    }
                }

                // Update modified properties:
                float mass = rigidBody.getMass();
                if(btRigidBody.getMass() != mass) btRigidBody.setMass(mass);
                float bounce = rigidBody.getBounce();
                if(btRigidBody.getBounce() != bounce) btRigidBody.setBounce(bounce);

                // Cache the transform and upload to simulation:
                Matrix4f transform = rigidBody.getGlobalTransform();
                transformCache.add(transform);
                btRigidBody.setTransform(transform);
            }

            // Step The Simulation
            dynamicsWorld.stepSimulation((float)deltaTime);

            for(int i = 0; i < rigidBodies.size(); i++) {
                RigidBody rigidBody = rigidBodies.get(i);
                BTRigidBody btRigidBody = (BTRigidBody)getServerResourceOrNull(rigidBody.getResource());
                if(btRigidBody == null) continue;

                // Compute delta transform and apply it:

                Matrix4f globalDelta = btRigidBody.getTransform().mul(transformCache.get(i).inverse());

                Matrix4f local = rigidBody.getTransform().getMatrix();
                Matrix4f global = rigidBody.getGlobalTransform();
                Matrix4f parentTransform = local.inverse().mul(global);

                Matrix4f newGlobal = globalDelta.mul(global);
                Matrix4f newLocal = newGlobal.mul(parentTransform.inverse());

                Matrix4f localDelta = newLocal.mul(local.inverse());
                rigidBody.getTransform().applyMatrix(localDelta);
            }
        }

        try {
            Thread.sleep(1);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Timer deltaTimer;
    private List<Matrix4f> transformCache = new ArrayList<>();
}
