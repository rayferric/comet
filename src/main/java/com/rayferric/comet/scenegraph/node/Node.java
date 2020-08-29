package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.video.VideoEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Node {
    public Node() {
        name.set("Node");
        setTransform(new Transform());
    }

    public Node(Node other) {
        name.set(other.getName() + "~");

        setParent(other.getParent());

        setTransform(other.getTransform());
    }

    @Override
    public String toString() {
        return String.format("Node{translation=%s, rotation=%s, scale=%s}", getTransform().getTranslation(), getTransform().getRotation(), getTransform().getScale());
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
        synchronized(parentLock) {
            if(this.parent != null) {
                synchronized(this.parent.childrenLock) {
                    this.parent.children.remove(this);
                }
            }

            this.parent = parent;

            if(this.parent != null) {
                synchronized(this.parent.childrenLock) {
                    this.parent.children.add(this);
                }
            }
        }
        invalidateGlobalTransform();
    }

    /**
     * Returns a snapshot of all children.<br>
     * • Returns a copy of the original {@link ArrayList}.<br>
     * • The return value may be modified and read from freely.<br>
     * • May be called from any thread.
     *
     * @return iterable list of children
     */
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
        if(child != null) child.setParent(this);
    }

    public void removeChild(Node child) {
        if(child != null)
            child.setParent(null);
    }

    public void removeChild(String name) {
        Node child = getChild(name);
        if(child != null)
            child.setParent(null);
    }

    // <editor-fold desc="Translation, Rotation and Scale">

    /**
     * Retrieves current transform of the node.<br>
     * • Returns the a reference to the original object.<br>
     * • The return value must not be ever modified, but may be read from.<br>
     * • May be called from any thread.
     *
     * @return read-only transform
     */
    public Transform getTransform() {
        return transform.get();
    }

    /**
     * Sets the current transform of the node.<br>
     * • Does not copy the parameter.<br>
     * • The passed value must not be modified from now on, but may be read from.<br>
     * • May be called from any thread.
     *
     * @param transform read-only transform
     */
    public void setTransform(Transform transform) {
        this.transform.set(transform);
        invalidateGlobalTransform();
    }

    // </editor-fold>

    public Transform getGlobalTransform() {
        synchronized(globalTransformValidLock) {
            if(!globalTransformValid) updateGlobalTransform();
        }
        return globalTransformCache;
    }

    // <editor-fold desc="Internal API">

    /**
     * Calls {@link #init()} method of this node and all its descendants.<br>
     * • Is internally used by the engine.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread. (This implementation uses the main thread for full freedom of use.)
     */
    public void initAll() {
        System.out.println("Initializing: " + getName());
        init();
        for(Node child : getChildren())
            child.initAll();
    }

    /**
     * Calls {@link #update(double)} method of this node and all its descendants.<br>
     * • Is internally used by the engine.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param delta delta time of the update frame
     */
    public void updateAll(double delta) {
        update(delta);
        for(Node child : getChildren())
            child.updateAll(delta);
    }

    /**
     * Adds this node and all its descendants to corresponding lists in the supplied index.<br>
     * The layer index' constructor calls this method itself.<br>
     * • Is internally used by the {@link LayerIndex} constructor.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread, but only by the {@link LayerIndex} constructor.
     *
     * @param index the {@link LayerIndex} to save this hierarchy to
     */
    public void indexAll(LayerIndex index) {
        for(Node child : getChildren())
            child.indexAll(index);
    }

    // </editor-fold>

    // <editor-fold desc="Methods for The User to Override"

    protected void init() {}

    protected void update(double delta) {}

    // </editor-fold>

    private final AtomicReference<String> name = new AtomicReference<>();

    private Node parent = null;
    private final Object parentLock = new Object();

    private final List<Node> children = new ArrayList<>();
    private final Object childrenLock = new Object();

    private final AtomicReference<Transform> transform = new AtomicReference<>();

    private Transform globalTransformCache;
    private boolean globalTransformValid;
    private final Object globalTransformValidLock = new Object();

    private void updateGlobalTransform() {
        Node parent = getParent();
        if(parent != null) globalTransformCache = parent.getGlobalTransform().add(getTransform());
        else globalTransformCache = getTransform();
        globalTransformValid = true;
    }

    private void invalidateGlobalTransform() {
        synchronized(globalTransformValidLock) {
            globalTransformValid = false;
        }
        synchronized(childrenLock) {
            for(Node child : children)
                child.invalidateGlobalTransform();
        }
    }
}
