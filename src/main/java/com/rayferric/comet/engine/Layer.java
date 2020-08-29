package com.rayferric.comet.engine;

import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.Node;

import java.util.concurrent.atomic.AtomicReference;

public class Layer {
    public Layer() {
        getRoot().setName("Root");
        genIndex();
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

    /**
     * Returns current state of this layer's index.<br>
     * • May be called from any thread, the returned index is read-only.
     *
     * @return read-only layer index
     */
    public LayerIndex getIndex() {
        return index.get();
    }

    /**
     * Generates a new read-only {@link LayerIndex} from this layer and stores it for later use.<br>
     * • May be called from any thread.
     */
    public void genIndex() {
        LayerIndex index = new LayerIndex(getRoot());
        this.index.set(index);
    }

    private final AtomicReference<Camera> camera = new AtomicReference<>(null);
    private final AtomicReference<Node> root = new AtomicReference<>(new Node());
    private final AtomicReference<LayerIndex> index = new AtomicReference<>();
}
