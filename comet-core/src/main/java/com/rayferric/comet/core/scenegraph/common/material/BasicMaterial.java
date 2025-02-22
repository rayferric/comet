package com.rayferric.comet.core.scenegraph.common.material;

import com.rayferric.comet.core.math.Vector4f;
import com.rayferric.comet.core.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.core.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;

public class BasicMaterial extends Material {
    public BasicMaterial() {
        super(ADDRESS_COLOR + Vector4f.BYTES);

        synchronized(BASIC_SHADER_LOCK) {
            if(basicShader == null)
                basicShader = new BinaryShader(true, "shaders/basic.vert.spv", "shaders/basic.frag.spv");
        }
        basicShader.load();

        setShader(basicShader);

        setColor(new Vector4f(1));

        setColorMap(null);
    }

    public Vector4f getColor() {
        return readUniformVector4f(ADDRESS_COLOR);
    }

    public void setColor(Vector4f color) {
        writeUniformData(ADDRESS_COLOR, color.toArray());
    }

    public Texture getColorMap() {
        return getTexture(BINDING_COLOR);
    }

    public void setColorMap(Texture colorTex) {
        setTexture(BINDING_COLOR, colorTex);
    }

    private static final int ADDRESS_COLOR = 0;

    private static final int BINDING_COLOR = 0;

    private static final Object BASIC_SHADER_LOCK = new Object();
    private static Shader basicShader = null;
}
