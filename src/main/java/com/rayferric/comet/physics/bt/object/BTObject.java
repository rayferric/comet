package com.rayferric.comet.physics.bt.object;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.physics.bt.BTWorld;
import com.rayferric.comet.server.ServerResource;

public abstract class BTObject implements ServerResource {
    @Override
    public void destroy() {
        setWorld(null);
    }

    public CollisionObject getBtObj() {
        return btObj;
    }

    public Matrix4f getTransform() {
        Transform btTransform = new Transform();
        btTransform.setIdentity();
        btObj.getWorldTransform(btTransform);
        float[] array = new float[16];
        btTransform.getOpenGLMatrix(array);
        return new Matrix4f(array);
    }

    public void setTransform(Matrix4f transform) {
        Transform btTransform = new Transform();
        btTransform.setFromOpenGLMatrix(transform.toArray());
        setBtTransform(btTransform);
    }

    public void setCollisionCompound(CompoundShape compound) {
        btObj.setCollisionShape(collisionCompound = compound);
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

    public BTWorld getWorld() {
        return world;
    }

    public void setWorld(BTWorld world) {
        if(this.world != null) this.world.removeObject(this);
        if((this.world = world) != null)
            this.world.addObject(this);
    }

    public void applyProps() {
        if(propsChanged) {
            setWorld(world);
            propsChanged = false;
        }
    }

    public boolean isJustCreated() {
        return justCreated;
    }

    public void popJustCreated() {
        justCreated = false;
    }

    protected CollisionObject btObj;
    protected CompoundShape collisionCompound = new CompoundShape();
    protected short layer = 0b1, mask = 0b1;
    protected BTWorld world = null;
    protected boolean propsChanged = true, justCreated = true;

    protected void setBtTransform(Transform btTransform) {
        btObj.setWorldTransform(btTransform);
    }
}
