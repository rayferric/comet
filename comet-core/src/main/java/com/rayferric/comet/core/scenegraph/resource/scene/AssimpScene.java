package com.rayferric.comet.core.scenegraph.resource.scene;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.math.Matrix4f;
import com.rayferric.comet.core.scenegraph.common.Surface;
import com.rayferric.comet.core.scenegraph.common.material.Material;
import com.rayferric.comet.core.scenegraph.node.model.Model;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.ArrayMesh;
import com.rayferric.comet.core.scenegraph.resource.video.mesh.Mesh;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public abstract class AssimpScene extends Scene {
    public AssimpScene(String path) {
        super(path);
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                aiScene = aiImportFile(properties.path, aiProcess_Triangulate | aiProcess_CalcTangentSpace | aiProcess_JoinIdenticalVertices);
                if(aiScene == null || (aiScene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) > 0 || aiScene.mRootNode() == null)
                    throw new RuntimeException(aiGetErrorString());

                numMaterials = aiScene.mNumMaterials();
                numMeshes = aiScene.mNumMeshes();

                PointerBuffer aiMeshes = aiScene.mMeshes();
                if(aiMeshes == null)
                    throw new RuntimeException("Failed to read scene meshes.\n" + properties.path);

                geometries = new Mesh[numMeshes];
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

        return true;
    }

    @Override
    public boolean unload() {
        if(!super.unload()) return false;
        aiReleaseImport(aiScene);
        return true;
    }

    @Override
    public Node instantiate() {
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
            throw new RuntimeException("Failed to read scene surfaces.\n" + properties.path);

        Surface[] surfaces = new Surface[numMeshes];
        for(int i = 0; i < numMeshes; i++) {
            Mesh mesh = geometries[i];

            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Material material = materials[aiMesh.mMaterialIndex()];

            surfaces[i] = new Surface(mesh, material);
        }

        AINode aiRoot = aiScene.mRootNode();
        if(aiRoot == null)
            throw new RuntimeException("Failed to fetch root node of the scene.\n" + properties.path);

        Node root = processAiNode(aiRoot, surfaces);
        root.initAll();
        return root;
    }

    protected AIScene aiScene;

    protected abstract Material processAiMaterial(AIMaterial aiMaterial);

    private int numMaterials, numMeshes;
    private Mesh[] geometries;

    private Node processAiNode(AINode aiNode, Surface[] surfaces) {
        Node node;

        IntBuffer aiMeshes = aiNode.mMeshes();
        if(aiMeshes != null) {
            Model model = new Model();

            int numMeshes = aiNode.mNumMeshes();
            for(int i = 0; i < numMeshes; i++)
                model.addSurface(surfaces[aiMeshes.get(i)]);

            node = model;
        } else
            node = new Node();

        PointerBuffer aiChildren = aiNode.mChildren();
        if(aiChildren != null) {
            int numChildren = aiNode.mNumChildren();
            for(int i = 0; i < numChildren; i++) {
                AINode aiChild = AINode.create(aiChildren.get(i));
                node.addChild(processAiNode(aiChild, surfaces));
            }
        }

        node.setName(aiNode.mName().dataString());

        AIMatrix4x4 m = aiNode.mTransformation();
        node.getTransform().setMatrix(new Matrix4f(
                m.a1(), m.b1(), m.c1(), m.d1(),
                m.a2(), m.b2(), m.c2(), m.d2(),
                m.a3(), m.b3(), m.c3(), m.d3(),
                m.a4(), m.b4(), m.c4(), m.d4()
        ));

        return node;
    }

    private Mesh processAiMeshGeometry(AIMesh aiMesh) {
        // Read vertices:
        AIVector3D.Buffer aiPositions = aiMesh.mVertices();
        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();

        int numVertices = aiMesh.mNumVertices();
        float[] vertices = new float[numVertices * 11];

        for(int i = 0; i < numVertices; i++) {
            AIVector3D position = aiPositions.get(i);
            vertices[i * 11] = position.x();
            vertices[i * 11 + 1] = position.y();
            vertices[i * 11 + 2] = position.z();

            if(aiTexCoords != null) {
                AIVector3D texCoord = aiTexCoords.get(i);
                vertices[i * 11 + 3] = texCoord.x();
                vertices[i * 11 + 4] = texCoord.y();
            }

            if(aiNormals != null) {
                AIVector3D normal = aiNormals.get(i);
                vertices[i * 11 + 5] = normal.x();
                vertices[i * 11 + 6] = normal.y();
                vertices[i * 11 + 7] = normal.z();
            }

            if(aiTangents != null) {
                AIVector3D tangent = aiTangents.get(i);
                vertices[i * 11 + 8] = tangent.x();
                vertices[i * 11 + 9] = tangent.y();
                vertices[i * 11 + 10] = tangent.z();
            }
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
        return new ArrayMesh(new MeshData(vertices, indices));
    }
}
