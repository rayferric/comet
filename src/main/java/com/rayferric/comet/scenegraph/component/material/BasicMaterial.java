package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class BasicMaterial extends Material {
    public BasicMaterial() {
        super(12);

        synchronized(BASIC_SHADER_LOCK) {
            if(basicShader == null)
                basicShader = new BinaryShader(false, "src/main/resources/shaders/basic.vert.spv", "src/main/resources/shaders/basic.frag.spv");
            else if(!basicShader.isLoaded() && !basicShader.isLoading())
                basicShader.load();
        }
        setShader(basicShader);

        writeUniformData(ADDRESS_COLOR, new Vector3f(1).toArray());
    }

    public Vector3f getColor() {
        return readUniformVector3f(ADDRESS_COLOR);
    }

    public void setColor(Vector3f color) {
        writeUniformData(ADDRESS_COLOR, color.toArray());
    }

    public Texture getColorTex() {
        return getTexture(BINDING_COLOR_TEX);
    }

    public void setColorTex(Texture colorTex) {
        setTexture(BINDING_COLOR_TEX, colorTex);
    }

    private static final Object BASIC_SHADER_LOCK = new Object();
    private static Shader basicShader = null;

    private final int ADDRESS_COLOR = 0;
    private final int BINDING_COLOR_TEX = 0;
}
