package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.component.material.SpriteMaterial;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class Sprite extends Node {
    public Sprite(Texture texture) {
        setName("Sprite");
        setTexture(texture);
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public Texture getTexture() {
        return material.getColorMap();
    }

    public void setTexture(Texture texture) {
        material.setColorMap(texture);
    }

    public Texture getNormalMap() {
        return material.getNormalMap();
    }

    public void setNormalMap(Texture normalMap) {
        material.setNormalMap(normalMap);
    }

    public Vector2i getFrames() {
        return material.getFrames();
    }

    public void setFrames(Vector2i frames) {
        material.setFrames(frames);
    }

    public int getFrame() {
        return material.getFrame();
    }

    public void setFrame(int frame) {
        material.setFrame(frame);
    }

    public Material getMaterial() {
        return material;
    }

    private final SpriteMaterial material = new SpriteMaterial();
}
