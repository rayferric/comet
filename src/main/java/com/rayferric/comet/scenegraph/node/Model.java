package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.scenegraph.resource.video.material.Material;
import com.rayferric.comet.scenegraph.resource.video.mesh.Mesh;

import java.util.concurrent.atomic.AtomicReference;

public class Model extends Node {
    public Model(Mesh mesh, Material material) {
        setName("Model");
        this.mesh.set(mesh);
        this.material.set(material);
    }

    public Mesh getMesh() {
        return mesh.get();
    }

    public void setMesh(Mesh mesh) {
        this.mesh.set(mesh);
    }

    public Material getMaterial() {
        return material.get();
    }

    public void setMaterial(Material material) {
        this.material.set(material);
    }

    private final AtomicReference<Mesh> mesh = new AtomicReference<>();
    private final AtomicReference<Material> material = new AtomicReference<>();
}
