package com.rayferric.comet.scenegraph.resource.scene;

import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.component.material.GLTFMaterial;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.util.ResourceLoader;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.assimp.Assimp.*;

public class GLTFScene extends AssimpScene {
    public GLTFScene(String path) {
        super(path);
        sceneDir = ResourceLoader.getDir(path);
    }

    @Override
    protected Material processAiMaterial(AIMaterial aiMaterial) {
        GLTFMaterial material = new GLTFMaterial();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buf = stack.mallocInt(1);
            IntBuffer max = stack.mallocInt(1);
            max.put(1).flip();
            aiGetMaterialIntegerArray(aiMaterial, AI_MATKEY_TWOSIDED, aiTextureType_NONE, 0, buf, max);
            if(buf.get() > 0) material.setCulling(false);
        }

        AIColor4D aiColor = AIColor4D.create();
        aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, aiColor);
        material.setColor(new Vector4f(aiColor.r(), aiColor.g(), aiColor.b(), aiColor.a()));
        aiColor.clear();
        aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, aiColor);
        material.setEmissive(new Vector3f(aiColor.r(), aiColor.g(), aiColor.b()));

        AIString aiString = AIString.create();

        Assimp.aiGetMaterialTexture(aiMaterial, AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_BASE_COLOR_TEXTURE, 0, aiString, (IntBuffer)null, null, null, null, null, null);
        String fileName = aiString.dataString();
        if(!fileName.isEmpty())
            material.setColorMap(getCachedTexture(sceneDir + fileName));

        aiString.clear();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiString, (IntBuffer)null, null, null, null, null, null);
        fileName = aiString.dataString();
        if(!fileName.isEmpty())
            material.setNormalMap(getCachedTexture(sceneDir + fileName));

        aiString.clear();
        Assimp.aiGetMaterialTexture(aiMaterial, AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLICROUGHNESS_TEXTURE, 0, aiString, (IntBuffer)null, null, null, null, null, null);
        fileName = aiString.dataString();
        if(!fileName.isEmpty())
            material.setMetallicRoughnessMap(getCachedTexture(sceneDir + fileName));

        aiString.clear();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_LIGHTMAP, 0, aiString, (IntBuffer)null, null, null, null, null, null);
        fileName = aiString.dataString();
        if(!fileName.isEmpty())
            material.setOcclusionMap(getCachedTexture(sceneDir + fileName));

        aiString.clear();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_EMISSIVE, 0, aiString, (IntBuffer)null, null, null, null, null, null);
        fileName = aiString.dataString();
        if(!fileName.isEmpty())
            material.setEmissiveMap(getCachedTexture(sceneDir + fileName));

        return material;
    }

    private static final HashMap<String, Texture> TEXTURE_CACHE = new HashMap<>();

    private final String sceneDir;

    private static Texture getCachedTexture(String path) {
        synchronized(TEXTURE_CACHE) {
            Texture cached = TEXTURE_CACHE.get(path);
            if(cached == null) {
                Texture tex = new ImageTexture(false, path, true);
                TEXTURE_CACHE.put(path, tex);
                return tex;
            } else return cached;
        }
    }
}
