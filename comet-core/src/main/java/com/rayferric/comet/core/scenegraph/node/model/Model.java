package com.rayferric.comet.core.scenegraph.node.model;

import com.rayferric.comet.core.engine.LayerIndex;
import com.rayferric.comet.core.scenegraph.common.Surface;
import com.rayferric.comet.core.scenegraph.node.Node;

import java.util.ArrayList;
import java.util.List;

public class Model extends Node {
    public Model() {
        setName("Model");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public synchronized List<Surface> snapSurfaces() {
        return new ArrayList<>(surfaces);
    }

    public synchronized Surface getSurface(int index) {
        return surfaces.get(index);
    }

    public synchronized void addSurface(Surface surface) {
        surfaces.add(surface);
    }

    public synchronized void removeSurface(int index) {
        surfaces.remove(index);
    }

    private final List<Surface> surfaces = new ArrayList<>();
}
