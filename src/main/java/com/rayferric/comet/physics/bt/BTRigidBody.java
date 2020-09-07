package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.server.ServerResource;

public class BTRigidBody implements ServerResource {
    public BTRigidBody() {
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
        rigidBody = new RigidBody(info);
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
    }

    @Override
    public void destroy() {
        // world.removeConstraint(...);
        if(world != null) world.removeRigidBody(rigidBody);
        rigidBody.destroy();
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    public void setWorld(DiscreteDynamicsWorld world) {
        if(world != null) world.removeRigidBody(rigidBody);
        (this.world = world).addRigidBody(rigidBody);
    }

    public Matrix4f getTransform() {
        Transform btTransform = new Transform();
        btTransform.setIdentity();
        rigidBody.getWorldTransform(btTransform);
        float[] array = new float[16];
        btTransform.getOpenGLMatrix(array);
        return new Matrix4f(array);
    }

    public void setTransform(Matrix4f transform) {
        Transform btTransform = new Transform();
        btTransform.setFromOpenGLMatrix(transform.toArray());
        rigidBody.setWorldTransform(btTransform);
        rigidBody.getMotionState().setWorldTransform(btTransform);
    }

    public void setCollisionCompound(CompoundShape compound) {
        rigidBody.setCollisionShape(collisionCompound = compound);
        setMass(mass);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
        collisionCompound.calculateLocalInertia(mass, inertia);
        world.removeRigidBody(rigidBody);
        rigidBody.setMassProps(mass, inertia);
        world.addRigidBody(rigidBody);
    }

    public float getBounce() {
        return bounce;
    }

    public void setBounce(float bounce) {
        world.removeRigidBody(rigidBody);
        rigidBody.setRestitution(this.bounce = bounce);
        world.addRigidBody(rigidBody);
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    private final RigidBody rigidBody;
    private DiscreteDynamicsWorld world = null;
    private CompoundShape collisionCompound = new CompoundShape();
    private float mass = 1, bounce = 0;
}
