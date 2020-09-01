package com.rayferric.comet.scenegraph.node.model;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.AABB;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.video.VideoEngine;

import java.util.ArrayList;
import java.util.Arrays;
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

    public synchronized List<Mesh> snapMeshes() {
        return new ArrayList<>(meshes);
    }

    public synchronized Mesh getMesh(int index) {
        return meshes.get(index);
    }

    public synchronized void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public synchronized void removeMesh(int index) {
        meshes.remove(index);
    }

    private final List<Mesh> meshes = new ArrayList<>();
}
