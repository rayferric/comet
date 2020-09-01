package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class FontMaterial extends Material {
    public FontMaterial() {
        super(ADDRESS_SHOW_BOUNDS + Integer.BYTES);

        synchronized(FONT_SHADER_LOCK) {
            if(fontShader == null)
                fontShader = new BinaryShader(true, "shaders/font.vert.spv", "shaders/font.frag.spv");
        }
        fontShader.load();

        setShader(fontShader);
        setTranslucent(true);

        setColor(new Vector4f(1));
        setCutoff(0.5F);
        setSoftness(0.1F);
        setShowBounds(false);

        setAtlas(null);
    }

    public Vector4f getColor() {
        return readUniformVector4f(ADDRESS_COLOR);
    }

    public void setColor(Vector4f color) {
        writeUniformData(ADDRESS_COLOR, color.toArray());
    }

    public float getCutoff() {
        return readUniformFloat(ADDRESS_CUTOFF);
    }

    public void setCutoff(float cutoff) {
        writeUniformData(ADDRESS_CUTOFF, new float[] { cutoff });
    }

    public float getSoftness() {
        return readUniformFloat(ADDRESS_SOFTNESS);
    }

    public void setSoftness(float softness) {
        writeUniformData(ADDRESS_SOFTNESS, new float[] { softness });
    }

    public boolean getShowBounds() {
        return readUniformInt(ADDRESS_SHOW_BOUNDS) == 1;
    }

    public void setShowBounds(boolean show) {
        writeUniformData(ADDRESS_SHOW_BOUNDS, new int[] { show ? 1 : 0 });
    }

    public Texture getAtlas() {
        return getTexture(BINDING_ATLAS);
    }

    public void setAtlas(Texture atlas) {
        setTexture(BINDING_ATLAS, atlas);
    }

    private static final int ADDRESS_COLOR = 0;
    private static final int ADDRESS_CUTOFF = nextStd140(ADDRESS_COLOR, Vector4f.BYTES, Float.BYTES);
    private static final int ADDRESS_SOFTNESS = nextStd140(ADDRESS_CUTOFF, Float.BYTES, Float.BYTES);
    private static final int ADDRESS_SHOW_BOUNDS = nextStd140(ADDRESS_SOFTNESS, Float.BYTES, Integer.BYTES);

    private static final int BINDING_ATLAS = 0;

    private static final Object FONT_SHADER_LOCK = new Object();
    private static Shader fontShader = null;
}
