package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RayCast extends Node {
    public RayCast() {
        setName("Ray Cast");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    // <editor-fold desc="Internal API">

    public void internalSetCollisionBody(PhysicsBody body) {
        collisionBody.set(body);
    }

    public void internalSetCollisionNormal(Vector3f normal) {
        collisionNormal.set(normal);
    }

    // </editor-fold>

    public Vector3f getVector() {
        return vector.get();
    }

    public void setVector(Vector3f vector) {
        this.vector.set(vector);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public boolean getIgnoreParent() {
        return ignoreParent.get();
    }

    public void setIgnoreParent(boolean ignore) {
        this.ignoreParent.set(ignore);
    }

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

    public PhysicsBody getCollisionBody() {
        return collisionBody.get();
    }

    public Vector3f getCollisionNormal() {
        return collisionNormal.get();
    }

    private final AtomicReference<Vector3f> vector = new AtomicReference<>(Vector3f.FORWARD);
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final AtomicBoolean ignoreParent = new AtomicBoolean(true);
    private final AtomicInteger layer = new AtomicInteger(0b1);
    private final AtomicInteger mask = new AtomicInteger(0b1);
    private final AtomicReference<PhysicsBody> collisionBody = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> collisionNormal = new AtomicReference<>(Vector3f.ZERO);
}
