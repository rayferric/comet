package com.rayferric.comet.engine;

import com.rayferric.comet.scenegraph.node.Camera;
import com.rayferric.comet.scenegraph.node.Node;

import java.util.concurrent.atomic.AtomicReference;

public class Layer {
    public Layer() {
        getRoot().setName("Root");
    }

    public Camera getCamera() {
        return camera.get();
    }

    public void setCamera(Camera camera) {
        this.camera.set(camera);
    }

    public Node getRoot() {
        return root.get();
    }

    private final AtomicReference<Camera> camera = new AtomicReference<>(null);
    private final AtomicReference<Node> root = new AtomicReference<>(new Node());
}
