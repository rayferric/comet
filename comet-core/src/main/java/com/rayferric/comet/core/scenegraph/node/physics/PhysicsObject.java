package com.rayferric.comet.core.scenegraph.node.physics;

import com.rayferric.comet.core.engine.LayerIndex;
import com.rayferric.comet.core.scenegraph.common.Collider;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.resource.physics.PhysicsResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PhysicsObject extends Node {
    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

//    public boolean internalColliderNeedsUpdate() {
//        return colliderNeedsUpdate.get();
//    }
//
//    public void internalPopColliderNeedsUpdate() {
//        colliderNeedsUpdate.set(false);
//    }

    public boolean internalBeginColliderUpdate() {
        if(colliderNeedsUpdate.get()) {
            colliderUpdateLock.lock();
            return true;
        } else return false;
    }

    public void internalEndColliderUpdate(boolean needsUpdate) {
        colliderNeedsUpdate.set(needsUpdate);
        colliderUpdateLock.unlock();
    }

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
        try {
            colliderUpdateLock.lock();
            synchronized(colliders) {
                colliders.add(collider);
            }
            colliderNeedsUpdate.set(true);
        } finally {
            colliderUpdateLock.unlock();
        }
    }

    public void removeCollider(int index) {
        try {
            colliderUpdateLock.lock();
            synchronized(colliders) {
                colliders.remove(index);
            }
            colliderNeedsUpdate.set(true);
        } finally {
            colliderUpdateLock.unlock();
        }
    }

    // </editor-fold>

    // <editor-fold desc="Layers and Masks">

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

    // </editor-fold>

    public PhysicsResource getResource() {
        return resource;
    }

    protected PhysicsResource resource;

    private final List<Collider> colliders = new ArrayList<>();
    private final AtomicBoolean colliderNeedsUpdate = new AtomicBoolean(true);
    private final Lock colliderUpdateLock = new ReentrantLock();
    private final AtomicInteger layer = new AtomicInteger(0b1);
    private final AtomicInteger mask = new AtomicInteger(0b1);
}
