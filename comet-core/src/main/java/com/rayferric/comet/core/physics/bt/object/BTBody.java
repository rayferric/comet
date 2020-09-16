package com.rayferric.comet.core.physics.bt.object;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.physics.bt.BTPhysicsEngine;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;

public class BTBody extends BTObject {
    public BTBody(PhysicsBody owner) {
        Transform bodyTransform = new Transform();
        bodyTransform.setIdentity();
        DefaultMotionState motionState = new DefaultMotionState(bodyTransform);

        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);

        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(
                mass,
                motionState,
                collisionCompound,
                inertia
        );
        btObj = new RigidBody(info);
        btObj.setUserPointer(owner);

        btObj.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        setKinematic(kinematic);
        btObj.setFriction(0.75F);
        btObj.setRestitution(0);
        getBtBody().setDamping(0.5F, 0.05F);
    }

    @Override
    public void destroy() {
        // world.removeConstraint(...);
        super.destroy();
        getBtBody().destroy();
    }

    @Override
    public void setCollisionCompound(CompoundShape compound) {
        super.setCollisionCompound(compound);
        setMass(mass);
    }

    public RigidBody getBtBody() {
        return (RigidBody)btObj;
    }

    public boolean isKinematic() {
        return kinematic;
    }

    public void setKinematic(boolean kinematic) {
        int flags = btObj.getCollisionFlags();
        if(this.kinematic = kinematic)
            flags |= CollisionFlags.KINEMATIC_OBJECT;
        else
            flags &= ~CollisionFlags.KINEMATIC_OBJECT;
        btObj.setCollisionFlags(flags);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);
        getBtBody().setMassProps(mass, inertia);
        setWorld(getWorld());
    }

    public float getFriction() {
        return btObj.getFriction();
    }

    public void setFriction(float friction) {
        btObj.setFriction(friction);
    }

    public float getBounce() {
        return btObj.getRestitution();
    }

    public void setBounce(float bounce) {
        btObj.setRestitution(bounce);
    }

    public float getLinearDrag() {
        return getBtBody().getLinearDamping();
    }

    public void setLinearDrag(float drag) {
        getBtBody().setDamping(drag, getBtBody().getAngularDamping());
    }

    public float getAngularDrag() {
        return getBtBody().getAngularDamping();
    }

    public void setAngularDrag(float drag) {
        getBtBody().setDamping(getBtBody().getLinearDamping(), drag);
    }

    public float getAngularFactor() {
        return getBtBody().getAngularFactor();
    }

    public void setAngularFactor(float factor) {
        getBtBody().setAngularFactor(factor);
    }

    public Vector3f getLinearVelocity() {
        javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f();
        getBtBody().getLinearVelocity(velocity);
        return BTPhysicsEngine.fromBtVector(velocity);
    }

    public void setLinearVelocity(Vector3f velocity) {
        getBtBody().setLinearVelocity(BTPhysicsEngine.toBtVector(velocity));
    }

    public Vector3f getAngularVelocity() {
        javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f();
        getBtBody().getAngularVelocity(velocity);
        return BTPhysicsEngine.fromBtVector(velocity);
    }

    public void setAngularVelocity(Vector3f velocity) {
        getBtBody().setAngularVelocity(BTPhysicsEngine.toBtVector(velocity));
    }

    public void applyForce(PhysicsBody.Force force) {
        Vector3f value = force.getValue();
        Vector3f pos = force.getPos();

        RigidBody btBody = getBtBody();
        switch(force.getType()) {
            case FORCE -> {
                if(pos == null) btBody.applyCentralForce(BTPhysicsEngine.toBtVector(value));
                else btBody.applyForce(BTPhysicsEngine.toBtVector(value), BTPhysicsEngine.toBtVector(pos));
            }
            case ACCELERATION -> {
                if(pos == null) btBody.applyCentralForce(BTPhysicsEngine.toBtVector(value.mul(mass)));
                else btBody.applyForce(BTPhysicsEngine.toBtVector(value.mul(mass)), BTPhysicsEngine.toBtVector(pos));
            }
            case FORCE_IMPULSE -> {
                if(pos == null) btBody.applyCentralImpulse(BTPhysicsEngine.toBtVector(value));
                else btBody.applyImpulse(BTPhysicsEngine.toBtVector(value), BTPhysicsEngine.toBtVector(pos));
            }
            case ACCELERATION_IMPULSE -> {
                if(pos == null) btBody.applyCentralImpulse(BTPhysicsEngine.toBtVector(value.mul(mass)));
                else btBody.applyImpulse(BTPhysicsEngine.toBtVector(value.mul(mass)), BTPhysicsEngine.toBtVector(pos));
            }
        }
    }

    public void clearForces() {
        getBtBody().clearForces();
    }

    @Override
    protected void setBtTransform(Transform btTransform) {
        super.setBtTransform(btTransform);
        getBtBody().getMotionState().setWorldTransform(btTransform);
    }

    private boolean kinematic = false;
    private float mass = 1;
}
