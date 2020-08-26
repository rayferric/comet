package com.rayferric.comet.scenegraph.component;

import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;

import java.util.concurrent.atomic.AtomicReference;

public class Mesh implements Component {
    public Mesh(Geometry geometry, Material material) {
        this.geometry.set(geometry);
        this.material.set(material);
    }

    public Geometry getGeometry() {
        return geometry.get();
    }

    public void setGeometry(Geometry geometry) {
        this.geometry.set(geometry);
    }

    public Material getMaterial() {
        return material.get();
    }

    public void setMaterial(Material material) {
        this.material.set(material);
    }

    private final AtomicReference<Geometry> geometry = new AtomicReference<>();
    private final AtomicReference<Material> material = new AtomicReference<>();
}
