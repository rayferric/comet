package com.rayferric.comet.core.physics.bt.object;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;
import com.rayferric.comet.core.math.Matrix4f;
import com.rayferric.comet.core.physics.bt.BTWorld;
import com.rayferric.comet.core.server.ServerResource;

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

    public void setLayer(short layer) {
        this.layer = layer;
        setWorld(getWorld());
    }

    public short getMask() {
        return mask;
    }

    public void setMask(short mask) {
        this.mask = mask;
        setWorld(getWorld());
    }

    public BTWorld getWorld() {
        return world;
    }

    public void setWorld(BTWorld world) {
        if(this.world != null) this.world.removeObject(this);
        if((this.world = world) != null)
            this.world.addObject(this);
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
    protected boolean justCreated = true;

    protected void setBtTransform(Transform btTransform) {
        btObj.setWorldTransform(btTransform);
    }
}
