package com.rayferric.comet.scenegraph.node.body;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.physics.PhysicsBodyResource;
import com.rayferric.comet.util.AtomicFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PhysicsBody extends Node {
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
}
