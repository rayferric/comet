package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.engine.Layer;
import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.physics.PhysicsEngine;
import com.rayferric.comet.physics.bt.object.BTArea;
import com.rayferric.comet.physics.bt.object.BTBody;
import com.rayferric.comet.physics.bt.object.BTObject;
import com.rayferric.comet.physics.bt.shape.*;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.physics.Area;
import com.rayferric.comet.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.scenegraph.node.physics.PhysicsObject;
import com.rayferric.comet.scenegraph.node.physics.RayCast;
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
        return new BTWorld();
    }

    @Override
    public ServerResource createBoxCollisionShape(Vector3f size) {
        return new BTBoxCollisionShape(size);
    }

    @Override
    public ServerResource createCapsuleCollisionShape(float radius, float height) {
        return new BTCapsuleCollisionShape(radius, height);
    }

    @Override
    public ServerResource createCylinderCollisionShape(float radius, float height) {
        return new BTCylinderCollisionShape(radius, height);
    }

    @Override
    public ServerResource createSphereCollisionShape(float radius) {
        return new BTSphereCollisionShape(radius);
    }

    @Override
    public ServerResource createPhysicsBody(PhysicsBody owner) {
        return new BTBody(owner);
    }

    @Override
    public ServerResource createArea(Area owner) {
        return new BTArea(owner);
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
            BTWorld btWorld = (BTWorld)getServerResourceOrNull(world);
            if(btWorld == null) continue;
            {
                Vector3f gravity = world.getGravity();
                if(!btWorld.getGravity().equals(gravity)) btWorld.setGravity(gravity);
            }

            List<PhysicsObject> objects = layerIndex.getPhysicsObjects();
            transformCache.clear();

            for(PhysicsObject obj : objects) {
                BTObject btObj = (BTObject)getServerResourceOrNull(obj.getResource());
                if(btObj == null) continue;

                // Ensure the right world:
                if(btObj.getWorld() != btWorld) btObj.setWorld(btWorld);

                // Update collider if needed:
                if(obj.internalColliderNeedsUpdate() || btObj.isJustCreated()) {
                    CompoundShape compound = new CompoundShape();
                    boolean aborted = false;
                    for(Collider collider : obj.snapColliders()) {
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
                        btObj.setCollisionCompound(compound);
                        obj.internalPopColliderNeedsUpdate();
                        btObj.popJustCreated();
                    }
                }

                // Update modified properties:
                short colLayer = obj.getLayer();
                if(btObj.getLayer() != colLayer) btObj.setLayer(colLayer);
                short colMask = obj.getMask();
                if(btObj.getMask() != colMask) btObj.setMask(colMask);

                // BTBody Specific
                if(obj instanceof PhysicsBody) {
                    PhysicsBody body = (PhysicsBody)obj;
                    BTBody btBody = (BTBody)btObj;

                    boolean kinematic = body.isKinematic();
                    if(btBody.isKinematic() != kinematic) btBody.setKinematic(kinematic);
                    float mass = body.getMass();
                    if(btBody.getMass() != mass) btBody.setMass(mass);
                    float friction = body.getFriction();
                    if(btBody.getFriction() != friction) btBody.setFriction(friction);
                    float bounce = body.getBounce();
                    if(btBody.getBounce() != bounce) btBody.setBounce(bounce);

                    // Properties that do not require removing the body from the world:
                    float linearDrag = body.getLinearDrag();
                    if(btBody.getLinearDrag() != linearDrag) btBody.setLinearDrag(linearDrag);
                    float angularDrag = body.getAngularDrag();
                    if(btBody.getAngularDrag() != angularDrag) btBody.setAngularDrag(angularDrag);
                    float angularFactor = body.getAngularFactor();
                    if(btBody.getAngularFactor() != angularFactor) btBody.setAngularFactor(angularFactor);

                    // Velocity and Forces
                    Vector3f nextLinearVelocity = body.internalPopNextLinearVelocity();
                    if(nextLinearVelocity != null) btBody.setLinearVelocity(nextLinearVelocity);
                    Vector3f nextAngularVelocity = body.internalPopNextAngularVelocity();
                    if(nextAngularVelocity != null) btBody.setAngularVelocity(nextAngularVelocity);
                    PhysicsBody.Force force;
                    while((force = body.internalPopForce()) != null) btBody.applyForce(force);
                    if(body.internalPopClearForces()) btBody.clearForces();
                }

                btObj.applyProps();

                // Cache the transform and upload to simulation:
                Matrix4f transform = obj.getGlobalTransform();
                transformCache.add(transform);
                btObj.setTransform(transform);
            }

            // Step The Simulation
            btWorld.step((float)deltaTime);

            // Test Ray Casts
            for(RayCast rayCast : layerIndex.getRayCasts())
                btWorld.processRayCast(rayCast);

            for(int i = 0; i < objects.size(); i++) {
                PhysicsObject obj = objects.get(i);
                BTObject btObj = (BTObject)getServerResourceOrNull(obj.getResource());
                if(btObj == null) continue;

                if(obj instanceof PhysicsBody) {
                    // Compute delta transform and apply it:
                    Matrix4f globalDelta = btObj.getTransform().mul(transformCache.get(i).inverse());

                    Matrix4f local = obj.getTransform().getMatrix();
                    Matrix4f global = obj.getGlobalTransform();
                    Matrix4f parentTransform = local.inverse().mul(global);

                    Matrix4f newGlobal = globalDelta.mul(global);
                    Matrix4f newLocal = newGlobal.mul(parentTransform.inverse());

                    Matrix4f localDelta = newLocal.mul(local.inverse());
                    obj.getTransform().applyMatrix(localDelta);

                    PhysicsBody body = (PhysicsBody)obj;
                    BTBody btBody = (BTBody)btObj;

                    body.internalUpdateLinearVelocity(btBody.getLinearVelocity());
                    body.internalUpdateAngularVelocity(btBody.getAngularVelocity());
                } else { // instanceof Area
                    Area area = (Area)obj;
                    BTArea btArea = (BTArea)btObj;

                    area.internalSetBodies(btArea.getBodies());
                    area.internalSetAreas(btArea.getAreas());
                }
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
