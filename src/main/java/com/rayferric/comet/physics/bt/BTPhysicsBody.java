package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
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
        btBody.setFriction(0.5F);
        btBody.setRestitution(0);
        btBody.setDamping(0, 0);
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
        btBody.getWorldTransform(btTransform);
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
        propsChanged = true;
    }

    public void setLayerBit(int bit, boolean state) {
        layer ^= ((state ? -1 : 0) ^ layer) & (1 << bit);
        propsChanged = true;
    }

    public short getMask() {
        return mask;
    }

    public boolean getMaskBit(int bit) {
        return (mask >> bit) > 0;
    }

    public void setMask(short mask) {
        this.mask = mask;
        propsChanged = true;
    }

    public void setMaskBit(int bit, boolean state) {
        mask ^= ((state ? -1 : 0) ^ mask) & (1 << bit);
        propsChanged = true;
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
        propsChanged = true;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);
        btBody.setMassProps(mass, inertia);
        propsChanged = true;
    }

    public float getFriction() {
        return btBody.getFriction();
    }

    public void setFriction(float friction) {
        btBody.setFriction(friction);
        propsChanged = true;
    }

    public float getBounce() {
        return btBody.getRestitution();
    }

    public void setBounce(float bounce) {
        btBody.setRestitution(bounce);
        propsChanged = true;
    }

    public void applyProps() {
        if(propsChanged) {
            setWorld(world);
            propsChanged = false;
        }
    }

    public float getLinearDrag() {
        return btBody.getLinearDamping();
    }

    public void setLinearDrag(float drag) {
        btBody.setDamping(drag, btBody.getAngularDamping());
        // propsChanged = true;
    }

    public float getAngularDrag() {
        return btBody.getAngularDamping();
    }

    public void setAngularDrag(float drag) {
        btBody.setDamping(btBody.getLinearDamping(), drag);
        // propsChanged = true;
    }

    public Vector3f getLinearVelocity() {
        javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f();
        btBody.getLinearVelocity(velocity);
        return fromBtVector(velocity);
    }

    public void setLinearVelocity(Vector3f velocity) {
        btBody.setLinearVelocity(toBtVector(velocity));
    }

    public Vector3f getAngularVelocity() {
        javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f();
        btBody.getAngularVelocity(velocity);
        return fromBtVector(velocity);
    }

    public void setAngularVelocity(Vector3f velocity) {
        btBody.setAngularVelocity(toBtVector(velocity));
    }

    public void applyForce(PhysicsBody.Force force) {
        Vector3f value = force.getValue();
        Vector3f pos = force.getPos();
        switch(force.getType()) {
            case FORCE -> {
                if(pos == null) btBody.applyCentralForce(toBtVector(value));
                else btBody.applyForce(toBtVector(value), toBtVector(pos));
            }
            case ACCELERATION -> {
                if(pos == null) btBody.applyCentralForce(toBtVector(value.mul(mass)));
                else btBody.applyForce(toBtVector(value.mul(mass)), toBtVector(pos));
            }
            case FORCE_IMPULSE -> {
                if(pos == null) btBody.applyCentralImpulse(toBtVector(value));
                else btBody.applyImpulse(toBtVector(value), toBtVector(pos));
            }
            case ACCELERATION_IMPULSE -> {
                if(pos == null) btBody.applyCentralImpulse(toBtVector(value.mul(mass)));
                else btBody.applyImpulse(toBtVector(value.mul(mass)), toBtVector(pos));
            }
        }
    }

    public void clearForces() {
        btBody.clearForces();
    }

    public RigidBody getBtBody() {
        return btBody;
    }

    public boolean isJustCreated() {
        return justCreated;
    }

    public void popJustCreated() {
        justCreated = false;
    }

    private final RigidBody btBody;
    private CompoundShape collisionCompound = new CompoundShape();
    private short layer = 0b1, mask = 0b1;
    private BTPhysicsWorld world = null;
    private boolean kinematic = false;
    private float mass = 1;
    private boolean propsChanged = true;
    private boolean justCreated = true;

    private static javax.vecmath.Vector3f toBtVector(Vector3f v) {
        return new javax.vecmath.Vector3f(v.getX(), v.getY(), v.getZ());
    }

    private static Vector3f fromBtVector(javax.vecmath.Vector3f v) {
        return new Vector3f(v.x, v.y, v.z);
    }
}
