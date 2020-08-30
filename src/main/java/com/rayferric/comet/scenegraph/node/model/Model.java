package com.rayferric.comet.scenegraph.node.model;

import com.rayferric.comet.engine.LayerIndex;
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

    public List<Mesh> snapMeshes() {
        synchronized(meshes) {
            return new ArrayList<>(meshes);
        }
    }

    public Mesh getMesh(int index) {
        synchronized(meshes) {
            return meshes.get(index);
        }
    }

    public void addMesh(Mesh mesh) {
        synchronized(meshes) {
            meshes.add(mesh);
        }
    }

    public void removeMesh(int index) {
        synchronized(meshes) {
            meshes.remove(index);
        }
    }

    private final List<Mesh> meshes = new ArrayList<>();
}
