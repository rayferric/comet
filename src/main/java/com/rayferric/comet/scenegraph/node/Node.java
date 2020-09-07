package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Node {
    public Node() {
        name.set("Node");
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

    /**
     * Retrieves current transform of the node.<br>
     * • Returns the reference to the underlying {@link Transform}.<br>
     * • Access to all {@link Transform} methods is synchronized.
     *
     * @return read-only transform
     */
    public Transform getTransform() {
        return transform;
    }

    public Matrix4f getGlobalTransform() {
        synchronized(globalTransformValidLock) {
            if(!globalTransformValid) {
                Node parent = getParent();
                if(parent != null) globalTransformCache = parent.getGlobalTransform().mul(transform.getMatrix());
                else globalTransformCache = transform.getMatrix();
                globalTransformValid = true;
            }
            return globalTransformCache;
        }
    }

    public void invalidateGlobalTransform() {
        synchronized(globalTransformValidLock) {
            globalTransformValid = false;
        }
        synchronized(childrenLock) {
            for(Node child : children)
                child.invalidateGlobalTransform();
        }
    }

    public boolean isVisible() {
        Node parent = getParent();
        return visible.get() && (parent == null || parent.isVisible());
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    // <editor-fold desc="Internal API">

    /**
     * Calls {@link #init()} method of this node and all its descendants.<br>
     * • Is internally used by the engine.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread. (This implementation uses the main thread for full freedom of use.)
     */
    public void initAll() {
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
        if(updateEnabled) update(delta);
        for(Node child : getChildren())
            child.updateAll(delta);
    }

    /**
     * Calls {@link #input(InputEvent)} method of this node and all its descendants.<br>
     * • Is internally used by the engine.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param events input events to be processed
     */
    public void inputAll(List<InputEvent> events) {
        if(inputEnabled) {
            for(InputEvent event : events)
                input(event);
        }
        for(Node child : getChildren())
            child.inputAll(events);
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

    // <editor-fold desc="Methods for The User to Override and Their Toggles"

    protected void init() {}

    protected void update(double delta) {}

    protected void input(InputEvent event) {}

    /**
     * Enables update processing on this node.<br>
     * • Must only be called from the constructor.
     */
    protected void enableUpdate() {
        updateEnabled = true;
    }

    /**
     * Enables input processing on this node.<br>
     * • Must only be called from the constructor.
     */
    protected void enableInput() {
        inputEnabled = true;
    }

    // </editor-fold>

    private final AtomicReference<String> name = new AtomicReference<>();

    private Node parent = null;
    private final Object parentLock = new Object();

    private final List<Node> children = new ArrayList<>();
    private final Object childrenLock = new Object();

    private final Transform transform = new Transform(this);

    private Matrix4f globalTransformCache;
    private boolean globalTransformValid = false;
    private final Object globalTransformValidLock = new Object();

    private final AtomicBoolean visible = new AtomicBoolean(true);

    private boolean updateEnabled = false, inputEnabled = false;
}