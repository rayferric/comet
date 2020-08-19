package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Node {
    public Node() {
        name.set("Node");

        setTranslation(new Vector3f(0));
        setRotation(new Vector3f(0));
        setScale(new Vector3f(1));

        updateLocalTransform();
    }

    public Node(Node other) {
        name.set(other.name + "~");

        setParent(other.parent);

        setTranslation(other.getTranslation());
        setRotation(other.getRotation());
        setScale(other.getScale());

        updateLocalTransform();
    }

    @Override
    public String toString() {
        return String.format("Node{translation=%s, rotation=%s, scale=%s}", translation, rotation, scale);
    }

    /**
     * This method is thread-safe.
     *
     * @return name
     */
    public String getName() {
        return name.get();
    }

    /**
     * This method is thread-safe.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    public Node getParent() {
        synchronized(parentLock) {
            return parent;
        }
    }

    public void setParent(Node parent) {
        // This may seem like a lot of redundant code,
        // but we must keep seamless sync order to prevent deadlocks
        synchronized(this.parent.childrenLock) {
            if(parent != null)
                synchronized(parent.childrenLock) {
                    synchronized(parentLock) {
                        if(this.parent != null) this.parent.children.remove(this);
                        this.parent = parent;
                        this.parent.children.add(this);
                    }
                }
            else
                synchronized(parentLock) {
                    if(this.parent != null) this.parent.children.remove(this);
                    this.parent = null;
                }
        }
        invalidateGlobalTransform();
    }

    public List<Node> getChildren() {
        synchronized(childrenLock) {
            return new ArrayList<>(children);
        }
    }

    public Node getChild(String name) {
        synchronized(childrenLock) {
            for(Node child : children)
                if(child.name.get().equals(name)) return child;
            return null;
        }
    }

    public void addChild(Node child) {
        if(child == null) return;
        synchronized(childrenLock) {
            synchronized(child.parentLock) {
                child.setParent(this);
            }
        }
    }

    public void removeChild(String name) {
        synchronized(childrenLock) {
            Node child = getChild(name);
            if(child == null) return;
            synchronized(child.parentLock) {
                child.setParent(null);
            }
        }
    }

    public Vector3f getTranslation() {
        return new Vector3f(translation.get());
    }

    public void setTranslation(Vector3f translation) {
        this.translation.set(new Vector3f(translation));
        localTransformValid.set(false);
        invalidateGlobalTransform();
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation.get());
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(new Vector3f(rotation));
        localTransformValid.set(false);
        invalidateGlobalTransform();
    }

    public Vector3f getScale() {
        return new Vector3f(scale.get());
    }

    public void setScale(Vector3f scale) {
        this.scale.set(new Vector3f(scale));
        localTransformValid.set(false);
        invalidateGlobalTransform();
    }

    public Matrix4f getLocalTransform() {
        synchronized(localTransformValid) {
            if(!localTransformValid.get()) updateLocalTransform();
        }
        return new Matrix4f(localTransformCache);
    }

    public Matrix4f getGlobalTransform() {
        synchronized(globalTransformValid) {
            if(!globalTransformValid.get()) updateGlobalTransform();
        }
        return new Matrix4f(globalTransformCache);
    }

    private final AtomicReference<String> name = new AtomicReference<>();

    private Node parent = null;
    private final List<Node> children = new ArrayList<>();

    private final Object parentLock = new Object();
    private final Object childrenLock = new Object();

    private final AtomicReference<Vector3f> translation = new AtomicReference<>();
    private final AtomicReference<Vector3f> rotation = new AtomicReference<>();
    private final AtomicReference<Vector3f> scale = new AtomicReference<>();

    private Matrix4f localTransformCache;
    private final AtomicBoolean localTransformValid = new AtomicBoolean();
    // localTransformValid is also the sync object

    private Matrix4f globalTransformCache;
    private final AtomicBoolean globalTransformValid = new AtomicBoolean();
    // globalTransformValid is also the sync object

    private void updateLocalTransform() {
        localTransformCache = Matrix4f.transform(translation.get(), rotation.get(), scale.get());
        localTransformValid.set(true);
    }

    private void updateGlobalTransform() {
        if(parent != null) globalTransformCache = parent.getGlobalTransform().mul(getLocalTransform());
        else globalTransformCache = getLocalTransform();
        globalTransformValid.set(true);
    }

    private void invalidateGlobalTransform() {
        globalTransformValid.set(false);
        for(Node node : children) node.invalidateGlobalTransform();
    }
}
