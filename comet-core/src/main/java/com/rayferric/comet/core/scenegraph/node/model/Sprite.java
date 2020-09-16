package com.rayferric.comet.core.scenegraph.node.model;

import com.rayferric.comet.core.math.Vector2f;
import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.scenegraph.common.Surface;
import com.rayferric.comet.core.scenegraph.common.material.SpriteMaterial;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.PlaneMesh;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;

public class Sprite extends Model {
    public Sprite() {
        setName("Sprite");

        synchronized(PLANE_MESH_LOCK) {
            if(planeMesh == null)
                planeMesh = new PlaneMesh(new Vector2f(1));
        }
        planeMesh.load();

        addSurface(new Surface(planeMesh, new SpriteMaterial()));
    }

    public SpriteMaterial getMaterial() {
        return (SpriteMaterial)getSurface(0).getMaterial();
    }

    public Texture getTexture() {
        return getMaterial().getColorMap();
    }

    public void setTexture(Texture texture) {
        getMaterial().setColorMap(texture);
    }

    public Texture getNormalMap() {
        return getMaterial().getNormalMap();
    }

    public void setNormalMap(Texture normalMap) {
        getMaterial().setNormalMap(normalMap);
    }

    public Vector2i getFrames() {
        return getMaterial().getFrames();
    }

    public void setFrames(Vector2i frames) {
        getMaterial().setFrames(frames);
    }

    public int getFrame() {
        return getMaterial().getFrame();
    }

    public void setFrame(int frame) {
        getMaterial().setFrame(frame);
    }

    private static final Object PLANE_MESH_LOCK = new Object();
    private static Mesh planeMesh = null;
}
