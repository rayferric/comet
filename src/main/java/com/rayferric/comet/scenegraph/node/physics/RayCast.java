package com.rayferric.comet.scenegraph.node.physics;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.Node;

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

    public void internalSetBody(PhysicsBody body) {
        this.body.set(body);
    }

    public void internalSetNormal(Vector3f normal) {
        this.normal.set(normal);
    }

    // </editor-fold>

    public Vector3f getVector() {
        return vector.get();
    }

    public void setVector(Vector3f vector) {
        this.vector.set(vector);
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
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

    public PhysicsBody getBody() {
        return body.get();
    }

    public Vector3f getNormal() {
        return normal.get();
    }

    private final AtomicReference<Vector3f> vector = new AtomicReference<>(Vector3f.FORWARD);
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean ignoreParent = new AtomicBoolean(true);
    private final AtomicInteger layer = new AtomicInteger(0b1);
    private final AtomicInteger mask = new AtomicInteger(0b1);
    private final AtomicReference<PhysicsBody> body = new AtomicReference<>(null);
    private final AtomicReference<Vector3f> normal = new AtomicReference<>(Vector3f.ZERO);
}
