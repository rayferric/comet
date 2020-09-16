package com.rayferric.comet.core.engine;

import com.rayferric.comet.core.scenegraph.node.camera.Camera;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.resource.physics.PhysicsWorld;

import java.util.concurrent.atomic.AtomicReference;

public class Layer {
    public Layer() {
        getRoot().setName("Root");
        genIndex();
    }

    public Node getRoot() {
        return root;
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    /**
     * Returns current state of this layer's index.<br>
     * • May be called from any thread, the returned index must not be modified.
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
        this.index.set(new LayerIndex(getRoot()));
    }

    public Camera getCamera() {
        return camera.get();
    }

    public void setCamera(Camera camera) {
        this.camera.set(camera);
    }

    private final Node root = new Node();
    private final PhysicsWorld physicsWorld = new PhysicsWorld();
    private final AtomicReference<LayerIndex> index = new AtomicReference<>();
    private final AtomicReference<Camera> camera = new AtomicReference<>(null);
}
