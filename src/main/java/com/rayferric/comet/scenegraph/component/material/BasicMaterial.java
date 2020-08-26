package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class BasicMaterial extends Material {
    public BasicMaterial() {
        super(ADDRESS_COLOR + Vector3f.BYTES);

        synchronized(BASIC_SHADER_LOCK) {
            if(basicShader == null)
                basicShader = new SourceShader(false, "data/shaders/basic.vert", "data/shaders/basic.frag");
            else if(!basicShader.isLoaded() && !basicShader.isLoading())
                basicShader.load();
        }
        setShader(basicShader);

        setColor(new Vector3f(1));
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

    private static final int ADDRESS_COLOR = 0;

    private static final int BINDING_COLOR_TEX = 0;

    private static final Object BASIC_SHADER_LOCK = new Object();
    private static Shader basicShader = null;
}
