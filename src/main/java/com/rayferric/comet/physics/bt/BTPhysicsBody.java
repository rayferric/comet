package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.server.ServerResource;

public class BTPhysicsBody implements ServerResource {
    public BTPhysicsBody() {
        Transform bodyTransform = new Transform();
        bodyTransform.setIdentity();
        DefaultMotionState motionState = new DefaultMotionState(bodyTransform);

        //CollisionShape boxShape = new BoxShape(new javax.vecmath.Vector3f(1, 1, 1));
        //Transform boxTransform = new Transform();
        //boxTransform.setIdentity();
        // collisionCompound.addChildShape(boxTransform, boxShape);
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);

        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(
                mass,
                motionState,
                collisionCompound,
                inertia
        );
        btBody = new RigidBody(info);
        btBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        setKinematic(kinematic);
        setFriction(friction);
        setBounce(bounce);
    }

    @Override
    public void destroy() {
        // world.removeConstraint(...);
        if(world != null) world.removeBody(this);
        btBody.destroy();
    }

    public Matrix4f getTransform() {
        Transform btTransform = new Transform();
        btTransform.setIdentity();
        btBody.getMotionState().getWorldTransform(btTransform);
        float[] array = new float[16];
        btTransform.getOpenGLMatrix(array);
        return new Matrix4f(array);
    }

    public void setTransform(Matrix4f transform) {
        Transform btTransform = new Transform();
        btTransform.setFromOpenGLMatrix(transform.toArray());
        btBody.setWorldTransform(btTransform);
        btBody.getMotionState().setWorldTransform(btTransform);
    }

    public void setCollisionCompound(CompoundShape compound) {
        btBody.setCollisionShape(collisionCompound = compound);
        setMass(mass);
    }

    public short getLayer() {
        return layer;
    }

    public boolean getLayerBit(int bit) {
        return (layer >> bit) > 0;
    }

    public void setLayer(short layer) {
        this.layer = layer;
    }

    public void setLayerBit(int bit, boolean state) {
        layer ^= ((state ? -1 : 0) ^ layer) & (1 << bit);
    }

    public short getMask() {
        return mask;
    }

    public boolean getMaskBit(int bit) {
        return (mask >> bit) > 0;
    }

    public void setMask(short mask) {
        this.mask = mask;
    }

    public void setMaskBit(int bit, boolean state) {
        mask ^= ((state ? -1 : 0) ^ mask) & (1 << bit);
    }

    public BTPhysicsWorld getWorld() {
        return world;
    }

    public void setWorld(BTPhysicsWorld world) {
        if(this.world != null) this.world.removeBody(this);
        if((this.world = world) != null)
            this.world.addBody(this, layer, mask);
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public void setKinematic(boolean kinematic) {
        int flags = btBody.getCollisionFlags();
        if(this.kinematic = kinematic)
            flags |= CollisionFlags.KINEMATIC_OBJECT;
        else
            flags &= ~CollisionFlags.KINEMATIC_OBJECT;
        btBody.setCollisionFlags(flags);
        setWorld(getWorld());
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);
        btBody.setMassProps(mass, inertia);
        setWorld(getWorld());
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        btBody.setFriction(this.friction = friction);
        setWorld(getWorld());
    }

    public float getBounce() {
        return bounce;
    }

    public void setBounce(float bounce) {
        btBody.setRestitution(this.bounce = bounce);
        setWorld(getWorld());
    }

    public RigidBody getBtBody() {
        return btBody;
    }

    private final RigidBody btBody;
    private CompoundShape collisionCompound = new CompoundShape();
    private short layer = 0b1, mask = 0b1;
    private BTPhysicsWorld world = null;
    private boolean kinematic = false;
    private float mass = 1, friction = 0.5F, bounce = 0;
}
