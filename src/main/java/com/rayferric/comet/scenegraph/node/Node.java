package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.math.Matrix4d;
import com.rayferric.comet.math.Vector3d;

import java.util.ArrayList;
import java.util.List;

// TODO Make this class thread-safe
public class Node {
    public Node() {
        name = "Node";

        setTranslation(new Vector3d(0));
        setRotation(new Vector3d(0));
        setScale(new Vector3d(1));

        updateTransform();
    }

    public Node(Node other) {
        name = other.name + "~";

        setParent(other.parent);

        setTranslation(other.translation);
        setRotation(other.rotation);
        setScale(other.scale);

        updateTransform();
    }

    @Override
    public String toString() {
        return String.format("Node{translation=%s, rotation=%s, scale=%s}", translation, rotation, scale);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        if(this.parent != null) this.parent.children.remove(this);
        this.parent = parent;
        if(this.parent != null) this.parent.children.add(this);
        invalidateGlobalTransform();
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    public Node getChild(String name) {
        for(Node child : children)
            if(child.name.equals(name)) return child;
        return null;
    }

    public void removeChild(String name) {
        Node child = getChild(name);
        if(child != null) child.setParent(null);
    }

    public void addChild(Node child) {
        if(child == null) return;
        child.setParent(this);
    }

    public Vector3d getTranslation() {
        return new Vector3d(translation);
    }

    public void setTranslation(Vector3d translation) {
        this.translation = new Vector3d(translation);
        transformValid = false;
        invalidateGlobalTransform();
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
        transformValid = false;
        invalidateGlobalTransform();
    }

    public Vector3d getScale() {
        return new Vector3d(scale);
    }

    public void setScale(Vector3d scale) {
        this.scale = new Vector3d(scale);
        transformValid = false;
        invalidateGlobalTransform();
    }

    public Matrix4d getTransform() {
        if(!transformValid) updateTransform();
        return new Matrix4d(transformCache);
    }

    public Matrix4d getGlobalTransform() {
        if(!globalTransformValid) updateGlobalTransform();
        return new Matrix4d(globalTransformCache);
    }

    private String name;

    private Node parent = null;
    private final List<Node> children = new ArrayList<>();

    private Vector3d translation;
    private Vector3d rotation;
    private Vector3d scale;

    private Matrix4d transformCache;
    private boolean transformValid;
    private Matrix4d globalTransformCache;
    private boolean globalTransformValid;

    private void updateTransform() {
        transformCache = Matrix4d.transform(translation, rotation, scale);
        transformValid = true;
    }

    private void updateGlobalTransform() {
        if(parent != null) globalTransformCache = parent.getGlobalTransform().mul(getTransform());
        else globalTransformCache = getTransform();
        globalTransformValid = true;
    }

    private void invalidateGlobalTransform() {
        globalTransformValid = false;
        for(Node node : children) node.invalidateGlobalTransform();
    }
}
