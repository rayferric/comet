package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.video.VideoEngine;

import java.util.concurrent.atomic.AtomicReference;

public class Model extends Node {
    public Model(Mesh mesh, Material material) {
        setName("Model");
        this.mesh.set(mesh);
        this.material.set(material);
    }

    @Override
    public void draw(VideoEngine videoEngine) {
        if(mesh == null) return;
        if(material == null) return;
        videoEngine.drawModel(this);
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
