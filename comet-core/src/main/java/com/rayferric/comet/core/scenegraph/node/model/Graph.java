package com.rayferric.comet.core.scenegraph.node.model;

import com.rayferric.comet.core.scenegraph.common.Surface;
import com.rayferric.comet.core.scenegraph.common.material.GraphMaterial;
import com.rayferric.comet.core.scenegraph.common.material.Material;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.GraphMesh;

import java.util.concurrent.atomic.AtomicReference;

public class Graph extends Model {
    public Graph() {
        setName("Graph");
        enableUpdate();

        addSurface(new Surface(null, new GraphMaterial()));
    }

    public Material getMaterial() {
        return getSurface(0).getMaterial();
    }

    public void setMaterial(Material material) {
       getSurface(0).setMaterial(material);
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

        if(nextMesh != null) {
            if(!nextMesh.isLoaded() || !nextMesh.isServerResourceReady()) return;

            Surface surface = getSurface(0);
            Mesh oldMesh = surface.getMesh();
            surface.setMesh(nextMesh);
            nextMesh = null;
            if(oldMesh != null) oldMesh.unload();
        }

        if(!needsUpdate) return;

        nextMesh = new GraphMesh(getValues());

        needsUpdate = false;
    }

    private final AtomicReference<float[]> values = new AtomicReference<>();
    private boolean needsUpdate = false;
    private Mesh nextMesh = null;

    private synchronized void requireUpdate() {
        needsUpdate = true;
    }
}
