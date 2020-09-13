package com.rayferric.comet.scenegraph.node.physics;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.resource.physics.PhysicsBodyResource;
import com.rayferric.comet.util.AtomicFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PhysicsBody extends PhysicsObject {
    /**
     * Describes the method of applying the force.
     * • FORCE - continuous force, must be cleared using #clearForces()
     * • ACCELERATION - continuous acceleration, must be cleared using #clearForces()
     * • FORCE_IMPULSE - single-frame force impulse, doesn't depend on mass
     * • ACCELERATION_IMPULSE - single-frame acceleration impulse, doesn't depend on mass
     */
    public enum ForceType {
        FORCE, ACCELERATION, FORCE_IMPULSE, ACCELERATION_IMPULSE
    }

    /**
     * Internal class used to pass data to the physics engine.
     */
    public static class Force {
        public Force(ForceType type, Vector3f value) {
            this.type = type;
            this.value = value;
            this.pos = null;
        }

        public Force(ForceType type, Vector3f value, Vector3f pos) {
            this.type = type;
            this.value = value;
            this.pos = pos;
        }

        public ForceType getType() {
            return type;
        }

        public Vector3f getValue() {
            return value;
        }

        public Vector3f getPos() {
            return pos;
        }

        private final ForceType type;
        private final Vector3f value;
        private final Vector3f pos;
    }

    public PhysicsBody() {
        setName("Physics Body");
        resource = new PhysicsBodyResource(this);
    }

    // <editor-fold desc="Internal API">

    public void internalUpdateLinearVelocity(Vector3f velocity) {
        currentLinearVelocity.set(velocity);
    }

    public Vector3f internalPopNextLinearVelocity() {
        return nextLinearVelocity.getAndSet(null);
    }

    public void internalUpdateAngularVelocity(Vector3f velocity) {
        currentAngularVelocity.set(velocity);
    }

    public Vector3f internalPopNextAngularVelocity() {
        return nextAngularVelocity.getAndSet(null);
    }

    public Force internalPopForce() {
        return forces.poll();
    }

    public boolean internalPopClearForces() {
        return resetForces.getAndSet(false);
    }

    // </editor-fold>

    public boolean isKinematic() {
        return kinematic.get();
    }

    public void setKinematic(boolean kinematic) {
        this.kinematic.set(kinematic);
    }

    public float getMass() {
        return mass.get();
    }

    public void setMass(float mass) {
        this.mass.set(mass);
    }

    public float getFriction() {
        return friction.get();
    }

    public void setFriction(float friction) {
        this.friction.set(friction);
    }

    public float getBounce() {
        return bounce.get();
    }

    public void setBounce(float bounce) {
        this.bounce.set(bounce);
    }

    public float getLinearDrag() {
        return linearDrag.get();
    }

    public void setLinearDrag(float drag) {
        linearDrag.set(drag);
    }

    public float getAngularDrag() {
        return angularDrag.get();
    }

    public void setAngularDrag(float drag) {
        angularDrag.set(drag);
    }

    public float getAngularFactor() {
        return angularFac.get();
    }

    public void setAngularFactor(float factor) {
        angularFac.set(factor);
    }

    // <editor-fold desc="Velocity and Forces">

    public Vector3f getLinearVelocity() {
        return currentLinearVelocity.get();
    }

    public void setLinearVelocity(Vector3f velocity) {
        nextLinearVelocity.set(velocity);
    }

    public Vector3f getAngularVelocity() {
        return currentAngularVelocity.get();
    }

    public void setAngularVelocity(Vector3f velocity) {
        nextAngularVelocity.set(velocity);
    }

    public void applyForce(ForceType type, Vector3f value) {
        forces.add(new Force(type, value));
    }

    public void applyForce(ForceType type, Vector3f value, Vector3f pos) {
        forces.add(new Force(type, value, pos));
    }

    public void clearForces() {
        resetForces.set(true);
    }

    // </editor-fold>

    private final AtomicBoolean kinematic = new AtomicBoolean(false);
    private final AtomicFloat mass = new AtomicFloat(1);
    private final AtomicFloat friction = new AtomicFloat(0.5F);
    private final AtomicFloat bounce = new AtomicFloat(0);
    private final AtomicFloat linearDrag = new AtomicFloat(0);
    private final AtomicFloat angularDrag = new AtomicFloat(0);
    private final AtomicFloat angularFac = new AtomicFloat(1);

    private final AtomicReference<Vector3f> nextLinearVelocity = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> currentLinearVelocity = new AtomicReference<>(Vector3f.ZERO);
    private final AtomicReference<Vector3f> nextAngularVelocity = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> currentAngularVelocity = new AtomicReference<>(Vector3f.ZERO);

    private final Queue<Force> forces = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean resetForces = new AtomicBoolean(false);
}
