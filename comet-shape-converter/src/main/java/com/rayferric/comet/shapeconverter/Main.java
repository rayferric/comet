package com.rayferric.comet.shapeconverter;

import com.rayferric.comet.shapeconverter.math.Matrix4f;
import com.rayferric.comet.shapeconverter.math.Vector3f;
import com.rayferric.comet.shapeconverter.math.Vector3i;
import com.rayferric.comet.shapeconverter.shape.ConcaveShape;
import com.rayferric.comet.shapeconverter.shape.Shape;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter path to the scene file:");
        String path = scanner.next();

        System.out.println("Importing the scene...");

        AIScene aiScene = aiImportFile(path, aiProcess_Triangulate | aiProcess_JoinIdenticalVertices);
        if(aiScene == null || (aiScene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) > 0 || aiScene.mRootNode() == null)
            throw new RuntimeException(aiGetErrorString());
        AINode aiRoot = aiScene.mRootNode();
        if(aiRoot == null)
            throw new RuntimeException("Could not identify scene root.");

        boolean concave = false;
        System.out.println("Do you want the collision shape to use concave geometry model (static bodies only)? (Y/N)");
        if(Character.toUpperCase(scanner.next().charAt(0)) == 'Y') {
            System.out.println("Concave geometry model will be used.");
            concave = true;
        } else System.out.println("Convex geometry model will be used.");

        scanner.close();

        System.out.println("Processing node tree...");

        List<Shape> shapes = new ArrayList<>();
        processAiNode(aiScene, aiRoot, Matrix4f.IDENTITY, concave, shapes);

        aiReleaseImport(aiScene);

        System.out.println("Writing to filesystem...");

        File file = new File("out.col");
        try {
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            stream.writeInt(shapes.size());

            for(Shape shape : shapes) {
                float[] transformFloats = shape.getTransform().toArray();
                for(float f : transformFloats) stream.writeFloat(f);

                stream.writeInt(shape.getPositions().size());
                for(Vector3f position : shape.getPositions()) {
                    float[] positionFloats = position.toArray();
                    for(float f : positionFloats) stream.writeFloat(f);
                }

                if(shape instanceof ConcaveShape) {
                    ConcaveShape concaveShape = (ConcaveShape)shape;

                    stream.writeInt(concaveShape.getTriangles().size());
                    for(Vector3i triangle : concaveShape.getTriangles()) {
                        int[] triangleInts = triangle.toArray();
                        for(int i : triangleInts) stream.writeInt(i);
                    }
                }
            }

            stream.close();
        } catch(Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.printf("Done, saved as \"%s\".\n", file.getPath());
    }

    private static void processAiNode(AIScene aiScene, AINode aiNode, Matrix4f parentTransform, boolean concave, List<Shape> shapes) {
        Matrix4f transform = fromAiMatrix(aiNode.mTransformation());
        Matrix4f globalTransform = transform.mul(parentTransform);

        IntBuffer aiMeshIndices = aiNode.mMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        if(aiMeshIndices != null && aiMeshes != null) {
            for(int i = 0; i < aiNode.mNumMeshes(); i++) {
                int aiMeshIdx = aiMeshIndices.get(i);
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(aiMeshIdx));

                System.out.println("Reading positions...");

                AIVector3D.Buffer aiPositions = aiMesh.mVertices();

                List<Vector3f> positions = new ArrayList<>(aiMesh.mNumVertices());

                for(int j = 0; j < aiMesh.mNumVertices(); j++) {
                    AIVector3D aiPosition = aiPositions.get(j);
                    Vector3f position = new Vector3f(aiPosition.x(), aiPosition.y(), aiPosition.z());
                    int found = positions.indexOf(position);
                    if(found != -1) continue;
                    positions.add(position);
                }

                if(!concave) {
                    shapes.add(new Shape(globalTransform, positions));
                    continue;
                }

                System.out.println("Indexing triangles...");

                AIFace.Buffer aiFaces = aiMesh.mFaces();
                List<Vector3i> triangles = new ArrayList<>(aiMesh.mNumFaces());

                for(int j = 0; j < aiMesh.mNumFaces(); j++) {
                    AIFace aiFace = aiFaces.get(j);
                    IntBuffer aiIndices = aiFace.mIndices();

                    Vector3i triangle = new Vector3i();

                    for(int k = 0; k < 3; k++) {
                        AIVector3D aiPosition = aiPositions.get(aiIndices.get(k));
                        Vector3f position = new Vector3f(aiPosition.x(), aiPosition.y(), aiPosition.z());

                        int found = positions.indexOf(position);
                        if(found == -1)
                            throw new RuntimeException("Requested position is not present in the array.");

                        switch(k) {
                            case 0 -> triangle.setX(found);
                            case 1 -> triangle.setY(found);
                            case 2 -> triangle.setZ(found);
                        }
                    }

                    triangles.add(triangle);
                }

                shapes.add(new ConcaveShape(globalTransform, positions, triangles));
            }
        }

        PointerBuffer aiChildren = aiNode.mChildren();
        if(aiChildren != null) {
            for(int i = 0; i < aiNode.mNumChildren(); i++) {
                AINode aiChild = AINode.create(aiChildren.get(i));
                processAiNode(aiScene, aiChild, globalTransform, concave, shapes);
            }
        }
    }

    private static Matrix4f fromAiMatrix(AIMatrix4x4 m) {
        return new Matrix4f(
                m.a1(), m.b1(), m.c1(), m.d1(),
                m.a2(), m.b2(), m.c2(), m.d2(),
                m.a3(), m.b3(), m.c3(), m.d3(),
                m.a4(), m.b4(), m.c4(), m.d4()
        );
    }
}