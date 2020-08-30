package com.rayferric.comet.scenegraph.node.model;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.component.material.SpriteMaterial;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.PlaneGeometry;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class Sprite extends Model {
    public Sprite() {
        setName("Sprite");

        synchronized(PLANE_GEOMETRY_LOCK) {
            if(planeGeometry == null)
                planeGeometry = new PlaneGeometry(new Vector2f(1));
        }
        planeGeometry.load();

        addMesh(new Mesh(planeGeometry, new SpriteMaterial()));
    }

    public SpriteMaterial getMaterial() {
        return (SpriteMaterial)getMesh(0).getMaterial();
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

    private static final Object PLANE_GEOMETRY_LOCK = new Object();
    private static Geometry planeGeometry = null;
}
