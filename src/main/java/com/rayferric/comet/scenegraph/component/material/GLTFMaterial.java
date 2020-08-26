package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class GLTFMaterial extends Material {
    public GLTFMaterial() {
        super(Vector4f.BYTES + Vector3f.BYTES);

        synchronized(GLTF_SHADER_LOCK) {
            if(gltfShader == null)
                gltfShader = new BinaryShader(false, "src/main/resources/shaders/gltf.vert.spv", "src/main/resources/shaders/gltf.frag.spv");
            else if(!gltfShader.isLoaded() && !gltfShader.isLoading())
                gltfShader.load();
        }
        setShader(gltfShader);

        setAlbedo(new Vector3f(1));
        setOccRghMtl(new Vector3f(1, 0, 0));
    }

    public Vector3f getAlbedo() {
        return readUniformVector3f(ADDRESS_ALBEDO);
    }

    public void setAlbedo(Vector3f albedo) {
        writeUniformData(ADDRESS_ALBEDO, albedo.toArray());
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

    public float getMetallic() {
        return readUniformFloat(ADDRESS_OCCLUSION);
    }

    public void setMetallic(float occlusion) {
        writeUniformData(ADDRESS_OCCLUSION, new float[] { metallic });
    }

    public Texture getAlbedoTex() {
        return getTexture(BINDING_ALBEDO_TEX);
    }

    public void setAlbedoTex(Texture tex) {
        setTexture(BINDING_ALBEDO_TEX, tex);
    }

    public Texture getNormalTex() {
        return getTexture(BINDING_NORMAL_TEX);
    }

    public void setNormalTex(Texture tex) {
        setTexture(BINDING_NORMAL_TEX, tex);
    }

    public Texture getOccRghMtlTex() {
        return getTexture(BINDING_OCC_RGH_MTL);
    }

    public void setOccRghMtlTex(Texture tex) {
        setTexture(BINDING_OCC_RGH_MTL, tex);
    }

    private static final int ADDRESS_ALBEDO = 0;
    private static final int ADDRESS_METALLIC = nextAddress(ADDRESS_ALBEDO, Vector3f.BYTES);
    private static final int ADDRESS_ROUGHNESS = nextAddress(ADDRESS_METALLIC, Float.BYTES);
    private static final int ADDRESS_OCCLUSION = nextAddress(ADDRESS_ROUGHNESS, Float.BYTES);
    private static final int ADDRESS_EMISSIVE = nextAddress(ADDRESS_OCCLUSION, Float.BYTES);

    private static final int BINDING_ALBEDO_TEX = 0;
    private static final int BINDING_NORMAL_TEX = 1;
    private static final int BINDING_METALLIC_ROUGHNESS = 2;
    private static final int BINDING_OCCLUSION = 3;
    private static final int BINDING_EMISSIVE = 4;

    private static final Object GLTF_SHADER_LOCK = new Object();
    private static Shader gltfShader = null;
}
