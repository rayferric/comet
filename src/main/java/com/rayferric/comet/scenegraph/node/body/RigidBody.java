package com.rayferric.comet.scenegraph.node.body;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.physics.RigidBodyResource;
import com.rayferric.comet.util.AtomicFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RigidBody extends Node {
    public RigidBody() {
        setName("Rigid Body");
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

    public float getMass() {
        return mass.get();
    }

    public void setMass(float mass) {
        this.mass.set(mass);
    }

    public float getBounce() {
        return bounce.get();
    }

    public void setBounce(float bounce) {
        this.bounce.set(bounce);
    }

    public RigidBodyResource getResource() {
        return resource;
    }

    private final RigidBodyResource resource = new RigidBodyResource();
    private final List<Collider> colliders = new ArrayList<>();
    private final AtomicBoolean colliderNeedsUpdate = new AtomicBoolean(true);
    private final AtomicFloat mass = new AtomicFloat(1);
    private final AtomicFloat bounce = new AtomicFloat(0);
}
