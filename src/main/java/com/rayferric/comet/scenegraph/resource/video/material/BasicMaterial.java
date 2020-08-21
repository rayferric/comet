package com.rayferric.comet.scenegraph.resource.video.material;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

import java.util.concurrent.atomic.AtomicReference;

public class BasicMaterial extends Material {
    public BasicMaterial(Vector3f color) {
        setShader(Engine.getInstance().getBasicShader());
        setColor(color);
    }

    public Vector3f getColor() {
        return new Vector3f(color.get());
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    public Texture getColorTex() {
        return colorTex.get();
    }

    public void setColorTex(Texture colorTex) {
        this.colorTex.set(colorTex);
    }

    private final AtomicReference<Vector3f> color = new AtomicReference<>();
    private final AtomicReference<Texture> colorTex = new AtomicReference<>(null);
}
