package com.rayferric.comet.scenegraph.node;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PhysicsBody extends Node {
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
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    // <editor-fold desc="Internal API">

    /**
     * Internal method used by the physics engine.
     *
     * @return whether the collider list was altered
     */
    public boolean colliderNeedsUpdate() {
        return colliderNeedsUpdate.get();
    }

    /**
     * Internal method used by the physics engine.
     */
    public void popColliderNeedsUpdate() {
        colliderNeedsUpdate.set(false);
    }

    /**
     * Internal method used by the physics engine.
     */
    public void updateLinearVelocity(Vector3f velocity) {
        currentLinearVelocity.set(velocity);
    }

    /**
     * Internal method used by the physics engine.
     *
     * @return linear velocity to update to
     */
    public Vector3f popNextLinearVelocity() {
        return nextLinearVelocity.getAndSet(null);
    }

    /**
     * Internal method used by the physics engine.
     */
    public void updateAngularVelocity(Vector3f velocity) {
        currentAngularVelocity.set(velocity);
    }

    /**
     * Internal method used by the physics engine.
     *
     * @return angular velocity to update to
     */
    public Vector3f popNextAngularVelocity() {
        return nextAngularVelocity.getAndSet(null);
    }

    /**
     * Internal method used by the physics engine.
     *
     * @return force to be applied or null
     */
    public Force popForce() {
        return forces.poll();
    }

    /**
     * Internal method used by the physics engine.
     *
     * @return whether the forces shall be cleared
     */
    public boolean popClearForces() {
        return resetForces.getAndSet(false);
    }

    // </editor-fold>

    // <editor-fold desc="Colliders">

    public List<Collider> snapColliders() {
        synchronized(colliders) {
            return new ArrayList<>(colliders);
        }
    }

    public Collider getCollider(int index) {
        synchronized(colliders) {
            return colliders.get(index);
        }
    }

    public void addCollider(Collider collider) {
        synchronized(colliders) {
            colliders.add(collider);
        }
        colliderNeedsUpdate.set(true);
    }

    public void removeCollider(int index) {
        synchronized(colliders) {
            colliders.remove(index);
        }
        colliderNeedsUpdate.set(true);
    }

    // </editor-fold>

    public short getLayer() {
        return (short)layer.get();
    }

    public boolean getLayerBit(int bit) {
        return (layer.get() >> bit) > 0;
    }

    public void setLayer(short layer) {
        this.layer.set(layer);
    }

    public void setLayerBit(int bit, boolean state) {
        layer.updateAndGet((layer) -> layer ^ (((state ? -1 : 0) ^ layer) & (1 << bit)));
    }

    public short getMask() {
        return (short)mask.get();
    }

    public boolean getMaskBit(int bit) {
        return (mask.get() >> bit) > 0;
    }

    public void setMask(short mask) {
        this.mask.set(mask);
    }

    public void setMaskBit(int bit, boolean state) {
        mask.updateAndGet((mask) -> mask ^ (((state ? -1 : 0) ^ mask) & (1 << bit)));
    }

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

    public Vector3f getLinearFactor() {
        return linearFac.get();
    }

    public void setLinearFactor(Vector3f factor) {
        linearFac.set(factor);
    }

    public Vector3f getAngularFactor() {
        return angularFac.get();
    }

    public void setAngularFactor(Vector3f factor) {
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

    public PhysicsBodyResource getResource() {
        return resource;
    }

    private final PhysicsBodyResource resource = new PhysicsBodyResource();
    private final List<Collider> colliders = new ArrayList<>();
    private final AtomicBoolean colliderNeedsUpdate = new AtomicBoolean(true);
    private final AtomicInteger layer = new AtomicInteger(0b1);
    private final AtomicInteger mask = new AtomicInteger(0b1);
    private final AtomicBoolean kinematic = new AtomicBoolean(false);
    private final AtomicFloat mass = new AtomicFloat(1);
    private final AtomicFloat friction = new AtomicFloat(0.5F);
    private final AtomicFloat bounce = new AtomicFloat(0);
    private final AtomicFloat linearDrag = new AtomicFloat(0);
    private final AtomicFloat angularDrag = new AtomicFloat(0);
    private final AtomicReference<Vector3f> linearFac = new AtomicReference<>(Vector3f.ONE);
    private final AtomicReference<Vector3f> angularFac = new AtomicReference<>(Vector3f.ONE);

    private final AtomicReference<Vector3f> nextLinearVelocity = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> currentLinearVelocity = new AtomicReference<>(Vector3f.ZERO);
    private final AtomicReference<Vector3f> nextAngularVelocity = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> currentAngularVelocity = new AtomicReference<>(Vector3f.ZERO);

    private final Queue<Force> forces = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean resetForces = new AtomicBoolean(false);
}
