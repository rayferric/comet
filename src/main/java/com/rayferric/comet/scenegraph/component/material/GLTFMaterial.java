package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector3i;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

public class GLTFMaterial extends Material {
    public GLTFMaterial() {
        super(ADDRESS_EMISSIVE + Vector3f.BYTES);

        synchronized(GLTF_SHADER_LOCK) {
            if(gltfShader == null)
                gltfShader = new SourceShader(false, "data/shaders/gltf.vert", "data/shaders/gltf.frag");
            else if(!gltfShader.isLoaded() && !gltfShader.isLoading())
                gltfShader.load();
        }
        setShader(gltfShader);

        setAlbedo(new Vector3f(1));
        setEmissive(new Vector3f(1));

        setAlbedoTex(null);
        setNormalTex(null);
        setMetallicRoughnessTex(null);
        setOcclusionTex(null);
        setEmissiveTex(null);
    }

    // <editor-fold desc="Uniforms">

    public Vector3f getAlbedo() {
        return readUniformVector3f(ADDRESS_ALBEDO);
    }

    public void setAlbedo(Vector3f albedo) {
        writeUniformData(ADDRESS_ALBEDO, albedo.toArray());
    }

    public Vector3f getEmissive() {
        return readUniformVector3f(ADDRESS_EMISSIVE);
    }

    public void setEmissive(Vector3f emissive) {
        writeUniformData(ADDRESS_EMISSIVE, emissive.toArray());
    }

    // </editor-fold>

    // <editor-fold desc="Textures">

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

    public Texture getMetallicRoughnessTex() {
        return getTexture(BINDING_METALLIC_ROUGHNESS);
    }

    public void setMetallicRoughnessTex(Texture tex) {
        setTexture(BINDING_METALLIC_ROUGHNESS, tex);
    }

    public Texture getOcclusionTex() {
        return getTexture(BINDING_OCCLUSION);
    }

    public void setOcclusionTex(Texture tex) {
        setTexture(BINDING_OCCLUSION, tex);
    }

    public Texture getEmissiveTex() {
        return getTexture(BINDING_EMISSIVE);
    }

    public void setEmissiveTex(Texture tex) {
        setTexture(BINDING_EMISSIVE, tex);
    }

    // </editor-fold>

    private static final int ADDRESS_ALBEDO = 0;
    private static final int ADDRESS_EMISSIVE = nextStd140(ADDRESS_ALBEDO, Vector3f.BYTES, Vector3f.BYTES);

    private static final int BINDING_ALBEDO_TEX = 0;
    private static final int BINDING_NORMAL_TEX = 1;
    private static final int BINDING_METALLIC_ROUGHNESS = 2;
    private static final int BINDING_OCCLUSION = 3;
    private static final int BINDING_EMISSIVE = 4;

    private static final Object GLTF_SHADER_LOCK = new Object();
    private static Shader gltfShader = null;
}
