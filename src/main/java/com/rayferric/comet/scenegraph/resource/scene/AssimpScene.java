package com.rayferric.comet.scenegraph.resource.scene;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.component.Mesh;
import com.rayferric.comet.scenegraph.node.Model;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.video.geometry.ArrayGeometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public abstract class AssimpScene extends Scene {
    public AssimpScene(String path) {
        super(path);
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                aiScene = aiImportFile(properties.path, aiProcess_Triangulate | aiProcess_CalcTangentSpace | aiProcess_JoinIdenticalVertices);
                if(aiScene == null || (aiScene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) > 0 || aiScene.mRootNode() == null)
                    throw new RuntimeException(aiGetErrorString());

                numMaterials = aiScene.mNumMaterials();
                numMeshes = aiScene.mNumMeshes();

                PointerBuffer aiMeshes = aiScene.mMeshes();
                if(aiMeshes == null)
                    throw new RuntimeException("Failed to read scene geometry.\n" + properties.path);

                geometries = new Geometry[numMeshes];
                for(int i = 0; i < numMeshes; i++) {
                    AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                    geometries[i] = processAiMeshGeometry(aiMesh);
                }

                finishLoading();
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    @Override
    public void unload() {
        super.unload();
        aiReleaseImport(aiScene);
    }

    @Override
    public Node[] instantiate() {
        if(!isLoaded())
            throw new IllegalStateException("Attempted to instantiate unloaded scene.\n" + properties.path);

        PointerBuffer aiMaterials = aiScene.mMaterials();
        if(aiMaterials == null)
            throw new RuntimeException("Failed to read scene materials.\n" + properties.path);

        Material[] materials = new Material[numMaterials];
        for(int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            materials[i] = processAiMaterial(aiMaterial);
        }

        PointerBuffer aiMeshes = aiScene.mMeshes();
        if(aiMeshes == null)
            throw new RuntimeException("Failed to read scene meshes.\n" + properties.path);

        Mesh[] meshes = new Mesh[numMeshes];
        for(int i = 0; i < numMeshes; i++) {
            Geometry geometry = geometries[i];

            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Material material = materials[aiMesh.mMaterialIndex()];

            meshes[i] = new Mesh(geometry, material);
        }

        AINode aiRoot = aiScene.mRootNode();
        if(aiRoot == null)
            throw new RuntimeException("Failed to fetch root node of a scene.\n" + properties.path);

        return new Node[] { processAiNode(aiRoot, meshes) };
    }

    protected AIScene aiScene;

    protected abstract Material processAiMaterial(AIMaterial aiMaterial);

    private int numMaterials, numMeshes;
    private Geometry[] geometries;

    private Node processAiNode(AINode aiNode, Mesh[] meshes) {
        Node node;

        IntBuffer aiMeshes = aiNode.mMeshes();
        if(aiMeshes != null) {
            int numMeshes = aiNode.mNumMeshes();
            List<Mesh> modelMeshes = new ArrayList<>(numMeshes);

            for(int i = 0; i < numMeshes; i++)
                modelMeshes.add(meshes[aiMeshes.get(i)]);

            node = new Model(modelMeshes.toArray(new Mesh[0]));
        } else
            node = new Node();

        PointerBuffer aiChildren = aiNode.mChildren();
        if(aiChildren != null) {
            int numChildren = aiNode.mNumChildren();
            for(int i = 0; i < numChildren; i++) {
                AINode aiChild = AINode.create(aiChildren.get(i));
                node.addChild(processAiNode(aiChild, meshes));
            }
        }

        node.setName(aiNode.mName().dataString());
        return node;
    }

    private Geometry processAiMeshGeometry(AIMesh aiMesh) {
        // Read vertices:
        AIVector3D.Buffer aiPositions = aiMesh.mVertices();
        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();

        if(aiTexCoords == null || aiNormals == null || aiTangents == null)
            throw new RuntimeException("Failed to read scene meshes.\n" + properties.path);

        int numVertices = aiMesh.mNumVertices();
        float[] vertices = new float[numVertices * 11];

        for(int i = 0; i < numVertices; i++) {
            AIVector3D position = aiPositions.get(i);
            AIVector3D texCoord = aiTexCoords.get(i);
            AIVector3D normal = aiNormals.get(i);
            AIVector3D tangent = aiTangents.get(i);

            vertices[i * 11] = position.x();
            vertices[i * 11 + 1] = position.y();
            vertices[i * 11 + 2] = position.z();
            vertices[i * 11 + 3] = texCoord.x();
            vertices[i * 11 + 4] = texCoord.y();
            vertices[i * 11 + 5] = normal.x();
            vertices[i * 11 + 6] = normal.y();
            vertices[i * 11 + 7] = normal.z();
            vertices[i * 11 + 8] = tangent.x();
            vertices[i * 11 + 9] = tangent.y();
            vertices[i * 11 + 10] = tangent.z();
        }

        // Read indices:
        AIFace.Buffer aiFaces = aiMesh.mFaces();

        int numFaces = aiMesh.mNumFaces();
        int[] indices = new int[numFaces * 3];

        for(int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer aiIndices = aiFace.mIndices();
            indices[i * 3] = aiIndices.get(0);
            indices[i * 3 + 1] = aiIndices.get(1);
            indices[i * 3 + 2] = aiIndices.get(2);
        }

        // Create the mesh:
        return new ArrayGeometry(new GeometryData(vertices, indices));
    }
}
