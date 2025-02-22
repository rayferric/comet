package com.rayferric.comet.core.scenegraph.common.material;

import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.math.Vector4f;
import com.rayferric.comet.core.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.core.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;

public class GLTFMaterial extends Material {
    public GLTFMaterial() {
        super(ADDRESS_EMISSIVE + Vector3f.BYTES);

        synchronized(GLTF_SHADER_LOCK) {
            if(gltfShader == null)
                gltfShader = new BinaryShader(true, "shaders/gltf.vert.spv", "shaders/gltf.frag.spv");
        }
        gltfShader.load();

        setShader(gltfShader);

        setColor(new Vector4f(1));
        setMetallic(1);
        setRoughness(1);
        setEmissive(new Vector3f(0));
        setUnlit(false);

        setColorMap(null);
        setNormalMap(null);
        setMetallicRoughnessMap(null);
        setOcclusionMap(null);
        setEmissiveMap(null);
    }

    // <editor-fold desc="Uniforms">

    public boolean isUnlit() {
        return readUniformInt(ADDRESS_UNLIT) != 0;
    }

    public void setUnlit(boolean unlit) {
        writeUniformData(ADDRESS_UNLIT, new int[] { unlit ? 1 : 0 });
    }

    public Vector4f getColor() {
        return readUniformVector4f(ADDRESS_COLOR);
    }

    public void setColor(Vector4f color) {
        writeUniformData(ADDRESS_COLOR, color.toArray());
    }

    public float getMetallic() {
        return readUniformFloat(ADDRESS_METALLIC);
    }

    public void setMetallic(float metallic) {
        writeUniformData(ADDRESS_METALLIC, new float[] { metallic });
    }

    public float getRoughness() {
        return readUniformFloat(ADDRESS_ROUGHNESS);
    }

    public void setRoughness(float roughness) {
        writeUniformData(ADDRESS_ROUGHNESS, new float[] { roughness });
    }

    public Vector3f getEmissive() {
        return readUniformVector3f(ADDRESS_EMISSIVE);
    }

    public void setEmissive(Vector3f emissive) {
        writeUniformData(ADDRESS_EMISSIVE, emissive.toArray());
    }

    // </editor-fold>

    // <editor-fold desc="Textures">

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

    public Texture getMetallicRoughnessMap() {
        return getTexture(BINDING_METALLIC_ROUGHNESS);
    }

    public void setMetallicRoughnessMap(Texture map) {
        setTexture(BINDING_METALLIC_ROUGHNESS, map);
    }

    public Texture getOcclusionMap() {
        return getTexture(BINDING_OCCLUSION);
    }

    public void setOcclusionMap(Texture map) {
        setTexture(BINDING_OCCLUSION, map);
    }

    public Texture getEmissiveMap() {
        return getTexture(BINDING_EMISSIVE);
    }

    public void setEmissiveMap(Texture map) {
        setTexture(BINDING_EMISSIVE, map);
    }

    // </editor-fold>

    private static final int ADDRESS_HAS_NORMAL_MAP = 0;
    private static final int ADDRESS_UNLIT = nextStd140(ADDRESS_HAS_NORMAL_MAP, Integer.BYTES, Integer.BYTES);
    private static final int ADDRESS_COLOR = nextStd140(ADDRESS_UNLIT, Integer.BYTES, Vector4f.BYTES);
    private static final int ADDRESS_METALLIC = nextStd140(ADDRESS_COLOR, Vector4f.BYTES, Float.BYTES);
    private static final int ADDRESS_ROUGHNESS = nextStd140(ADDRESS_METALLIC, Float.BYTES, Float.BYTES);
    private static final int ADDRESS_EMISSIVE = nextStd140(ADDRESS_ROUGHNESS, Float.BYTES, Vector3f.BYTES);

    private static final int BINDING_COLOR = 0;
    private static final int BINDING_NORMAL = 1;
    private static final int BINDING_METALLIC_ROUGHNESS = 2;
    private static final int BINDING_OCCLUSION = 3;
    private static final int BINDING_EMISSIVE = 4;

    private static final Object GLTF_SHADER_LOCK = new Object();
    private static Shader gltfShader = null;
}
