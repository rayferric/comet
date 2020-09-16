package com.rayferric.comet.core.scenegraph.common;

import com.rayferric.comet.core.scenegraph.common.material.Material;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.Mesh;

import java.util.concurrent.atomic.AtomicReference;

public class Surface {
    public Surface(Mesh mesh, Material material) {
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
