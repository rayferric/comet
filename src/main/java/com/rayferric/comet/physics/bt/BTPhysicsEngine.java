package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.bt.shape.BTBoxCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTSphereCollisionShape;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.body.PhysicsBody;
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
    public ServerResource createPhysicsBody() {
        return new BTPhysicsBody();
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

            List<PhysicsBody> bodies = layer.getIndex().getPhysicsBodies();
            transformCache.clear();

            for(PhysicsBody body : bodies) {
                BTPhysicsBody btBody = (BTPhysicsBody)getServerResourceOrNull(body.getResource());
                if(btBody == null) continue;

                // Ensure the right world:
                if(btBody.getWorld() != btWorld) btBody.setWorld(btWorld);

                // Update collider if needed:
                if(body.colliderNeedsUpdate()) {
                    CompoundShape compound = new CompoundShape();
                    boolean aborted = false;
                    for(Collider collider : body.snapColliders()) {
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
                        btBody.setCollisionCompound(compound);
                        body.popColliderNeedsUpdate();
                    }
                }

                // Update modified properties:
                short colLayer = body.getLayer();
                if(btBody.getLayer() != colLayer) btBody.setLayer(colLayer);
                short colMask = body.getMask();
                if(btBody.getMask() != colMask) btBody.setMask(colMask);
                boolean kinematic = body.isKinematic();
                if(btBody.isKinematic() != kinematic) btBody.setKinematic(kinematic);
                float mass = body.getMass();
                if(btBody.getMass() != mass) btBody.setMass(mass);
                float friction = body.getFriction();
                if(btBody.getFriction() != friction) btBody.setFriction(friction);
                float bounce = body.getBounce();
                if(btBody.getBounce() != bounce) btBody.setBounce(bounce);

                // Cache the transform and upload to simulation:
                Matrix4f transform = body.getGlobalTransform();
                transformCache.add(transform);
                btBody.setTransform(transform);
            }

            // Step The Simulation
            btWorld.step((float)deltaTime, 8);

            for(int i = 0; i < bodies.size(); i++) {
                PhysicsBody physicsBody = bodies.get(i);
                BTPhysicsBody btPhysicsBody = (BTPhysicsBody)getServerResourceOrNull(physicsBody.getResource());
                if(btPhysicsBody == null) continue;

                // Compute delta transform and apply it:

                Matrix4f globalDelta = btPhysicsBody.getTransform().mul(transformCache.get(i).inverse());

                Matrix4f local = physicsBody.getTransform().getMatrix();
                Matrix4f global = physicsBody.getGlobalTransform();
                Matrix4f parentTransform = local.inverse().mul(global);

                Matrix4f newGlobal = globalDelta.mul(global);
                Matrix4f newLocal = newGlobal.mul(parentTransform.inverse());

                Matrix4f localDelta = newLocal.mul(local.inverse());
                physicsBody.getTransform().applyMatrix(localDelta);
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
    private final List<Matrix4f> transformCache = new ArrayList<>();
}
