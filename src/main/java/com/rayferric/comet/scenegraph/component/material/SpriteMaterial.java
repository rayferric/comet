package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.math.Vector4i;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class SpriteMaterial extends Material {
    public SpriteMaterial() {
        super(ADDRESS_FRAME + Integer.BYTES);

        synchronized(SPRITE_SHADER_LOCK) {
            if(spriteShader == null)
                spriteShader = new SourceShader(false, "data/shaders/sprite.vert", "data/shaders/sprite.frag");
        }
        spriteShader.load();

        setShader(spriteShader);

        setColor(new Vector4f(1));
        setFrames(new Vector2i(1));
        setFrame(0);

        setColorMap(null);
        setNormalMap(null);
    }

    public Vector4f getColor() {
        return readUniformVector4f(ADDRESS_COLOR);
    }

    public void setColor(Vector4f color) {
        writeUniformData(ADDRESS_COLOR, color.toArray());
    }

    public Vector2i getFrames() {
        return readUniformVector2i(ADDRESS_FRAMES);
    }

    public void setFrames(Vector2i frames) {
        writeUniformData(ADDRESS_FRAMES, frames.toArray());
    }

    public int getFrame() {
        return readUniformInt(ADDRESS_FRAME);
    }

    public void setFrame(int frame) {
        writeUniformData(ADDRESS_FRAME, new int[] { frame });
    }

    public Texture getColorMap() {
        return getTexture(BINDING_COLOR);
    }

    public void setColorMap(Texture map) {
        setTexture(BINDING_COLOR, map);
    }

    public Texture getNormalMap() {
        return getTexture(BINDING_NORMAL);
    }

    public void setNormalMap(Texture map) {
        writeUniformData(ADDRESS_HAS_NORMAL_MAP, new int[] { map == null ? 0 : 1 });
        setTexture(BINDING_NORMAL, map);
    }

    private static final int ADDRESS_HAS_NORMAL_MAP = 0;
    private static final int ADDRESS_COLOR = nextStd140(ADDRESS_HAS_NORMAL_MAP, Integer.BYTES, Vector4f.BYTES);
    private static final int ADDRESS_FRAMES = nextStd140(ADDRESS_COLOR, Vector4f.BYTES, Vector2i.BYTES);
    private static final int ADDRESS_FRAME = nextStd140(ADDRESS_FRAMES, Vector2i.BYTES, Integer.BYTES);

    private static final int BINDING_COLOR = 0;
    private static final int BINDING_NORMAL = 1;

    private static final Object SPRITE_SHADER_LOCK = new Object();
    private static Shader spriteShader = null;
}
