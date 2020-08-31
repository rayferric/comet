package com.rayferric.comet.scenegraph.node.model;

import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.component.material.GraphMaterial;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.GraphGeometry;

import java.util.concurrent.atomic.AtomicReference;

public class Graph extends Model {
    public Graph() {
        setName("Graph");

        addMesh(new Mesh(null, new GraphMaterial()));
    }

    public Material getMaterial() {
        return getMesh(0).getMaterial();
    }

    public void setMaterial(Material material) {
       getMesh(0).setMaterial(material);
    }

    public float[] getValues() {
        return values.get();
    }

    public void setValues(float[] values) {
        this.values.set(values);
        requireUpdate();
    }

    @Override
    protected synchronized void update(double delta) {
        super.update(delta);

        if(nextGeometry != null) {
            if(!nextGeometry.isLoaded() || !nextGeometry.isServerResourceReady()) return;

            Mesh mesh = getMesh(0);
            Geometry oldGeometry = mesh.getGeometry();
            mesh.setGeometry(nextGeometry);
            nextGeometry = null;
            if(oldGeometry != null) oldGeometry.unload();
        }

        if(!needsUpdate) return;

        nextGeometry = new GraphGeometry(getValues());

        needsUpdate = false;
    }

    private final AtomicReference<float[]> values = new AtomicReference<>();
    private boolean needsUpdate = false;
    private Geometry nextGeometry = null;

    private synchronized void requireUpdate() {
        needsUpdate = true;
    }
}
