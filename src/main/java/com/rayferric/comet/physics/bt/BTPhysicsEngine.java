package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Quaternion;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.bt.shape.BTBoxCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTCapsuleCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTCollisionShape;
import com.rayferric.comet.physics.bt.shape.BTSphereCollisionShape;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
import com.rayferric.comet.scenegraph.node.RayCast;
import com.rayferric.comet.scenegraph.resource.physics.PhysicsWorld;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BTPhysicsEngine extends PhysicsEngine {
    public static final javax.vecmath.Vector3f BT_VECTOR_ZERO = new javax.vecmath.Vector3f(0, 0, 0);

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
    public ServerResource createCapsuleCollisionShape(float radius, float height) {
        return new BTCapsuleCollisionShape(radius, height);
    }

    @Override
    public ServerResource createPhysicsBody(PhysicsBody owner) {
        return new BTPhysicsBody(owner);
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
            LayerIndex layerIndex = layer.getIndex();

            PhysicsWorld world = layer.getPhysicsWorld();
            BTPhysicsWorld btWorld = (BTPhysicsWorld)getServerResourceOrNull(world);
            if(btWorld == null) continue;
            {
                Vector3f gravity = world.getGravity();
                if(!btWorld.getGravity().equals(gravity)) btWorld.setGravity(gravity);
            }

            List<PhysicsBody> bodies = layerIndex.getPhysicsBodies();
            transformCache.clear();

            for(PhysicsBody body : bodies) {
                BTPhysicsBody btBody = (BTPhysicsBody)getServerResourceOrNull(body.getResource());
                if(btBody == null) continue;

                // Ensure the right world:
                if(btBody.getWorld() != btWorld) btBody.setWorld(btWorld);

                // Update collider if needed:
                if(body.colliderNeedsUpdate() || btBody.isJustCreated()) {
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
                        btBody.popJustCreated();
                    }
                }

                // Update modified properties:
                short colLayer = body.getLayer();
                if(btBody.getLayer() != colLayer) btBody.setLayer(colLayer);
                short colMask = body.getMask();
                if(btBody.getMask() != colMask) btBody.setMask(colMask);
                boolean kinematic = body.isKinematic();
                if(btBody.isKinematic() != kinematic) btBody.setKinematic(kinematic);
                boolean gravityDisabled = body.isGravityDisabled();
                if(btBody.isGravityDisabled() != gravityDisabled) btBody.setGravityDisabled(gravityDisabled);
                float mass = body.getMass();
                if(btBody.getMass() != mass) btBody.setMass(mass);
                float friction = body.getFriction();
                if(btBody.getFriction() != friction) btBody.setFriction(friction);
                float bounce = body.getBounce();
                if(btBody.getBounce() != bounce) btBody.setBounce(bounce);

                btBody.applyProps();

                // Properties that do not require removing the body from the world:
                float linearDrag = body.getLinearDrag();
                if(btBody.getLinearDrag() != linearDrag) btBody.setLinearDrag(linearDrag);
                float angularDrag = body.getAngularDrag();
                if(btBody.getAngularDrag() != angularDrag) btBody.setAngularDrag(angularDrag);

                // Velocity and Forces
                Vector3f nextLinearVelocity = body.popNextLinearVelocity();
                if(nextLinearVelocity != null) btBody.setLinearVelocity(nextLinearVelocity);
                Vector3f nextAngularVelocity = body.popNextAngularVelocity();
                if(nextAngularVelocity != null) btBody.setAngularVelocity(nextAngularVelocity);
                PhysicsBody.Force force;
                while((force = body.popForce()) != null) btBody.applyForce(force);
                if(body.popClearForces()) btBody.clearForces();

                // Cache the transform and upload to simulation:
                Matrix4f transform = body.getGlobalTransform();
                transformCache.add(transform);
                btBody.setTransform(transform);
            }

            // Step The Simulation
            btWorld.step((float)deltaTime, 8);

            // Test Ray Casts
            for(RayCast rayCast : layerIndex.getRayCasts())
                btWorld.processRayCast(rayCast);

            for(int i = 0; i < bodies.size(); i++) {
                PhysicsBody body = bodies.get(i);
                BTPhysicsBody btBody = (BTPhysicsBody)getServerResourceOrNull(body.getResource());
                if(btBody == null) continue;

                body.updateLinearVelocity(btBody.getLinearVelocity());
                body.updateAngularVelocity(btBody.getAngularVelocity());

                // Compute delta transform and apply it:
                Matrix4f globalDelta = btBody.getTransform().mul(transformCache.get(i).inverse());

                // TODO Test if this quaternion-euler-quaternion routine doesn't introduce any bugs
                globalDelta.setTranslation(globalDelta.getTranslation().mul(body.getLinearFactor()));
                Vector3f eulerRotation = globalDelta.getRotation().toEuler();
                eulerRotation = eulerRotation.mul(body.getAngularFactor());
                globalDelta.setRotation(Quaternion.eulerAngle(eulerRotation));

                body.setAngularVelocity(body.getAngularVelocity().mul(body.getAngularFactor()));

                Matrix4f local = body.getTransform().getMatrix();
                Matrix4f global = body.getGlobalTransform();
                Matrix4f parentTransform = local.inverse().mul(global);

                Matrix4f newGlobal = globalDelta.mul(global);
                Matrix4f newLocal = newGlobal.mul(parentTransform.inverse());

                Matrix4f localDelta = newLocal.mul(local.inverse());
                body.getTransform().applyMatrix(localDelta);
            }
        }

        try {
            Thread.sleep(1);
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static javax.vecmath.Vector3f toBtVector(Vector3f v) {
        return new javax.vecmath.Vector3f(v.getX(), v.getY(), v.getZ());
    }

    public static Vector3f fromBtVector(javax.vecmath.Vector3f v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    private Timer deltaTimer;
    private final List<Matrix4f> transformCache = new ArrayList<>();
}
