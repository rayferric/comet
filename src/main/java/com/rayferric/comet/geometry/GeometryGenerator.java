package com.rayferric.comet.geometry;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeometryGenerator {
    public static GeometryData genPlane(Vector2f size, boolean shadeSmooth) {
        float sx = size.getX() * 0.5F;
        float sy = size.getY() * 0.5F;

        Vertex[] vertices = {
                new Vertex(-sx, -sy, 0, 0, 0),
                new Vertex(sx, -sy, 0, 1, 0),
                new Vertex(sx,  sy, 0, 1, 1),
                new Vertex(-sx,  sy, 0, 0, 1)
        };
        Face face = new Face(vertices);
        Triangle[] triangles = triangulate(new Face[] { face });
        return index(triangles, shadeSmooth);
    }

    private static Triangle[] triangulate(Face[] faces) {
        int triangleCount = 0;

        for(Face face : faces)
            triangleCount += face.getTriangleCount();

        List<Triangle> triangles = new ArrayList<>(triangleCount);

        for(Face face : faces)
            triangles.addAll(Arrays.asList(face.triangulate()));

        return triangles.toArray(new Triangle[0]);
    }

    private static GeometryData index(Triangle[] triangles, boolean shadeSmooth) {
        // Create unpacked data by simply concatenating triangles' vertices:

        Vertex[] unpackedVertices = new Vertex[triangles.length * 3];
        Vector3f[] unpackedNormals = new Vector3f[triangles.length * 3];
        Vector3f[] unpackedTangents = new Vector3f[triangles.length * 3];
        for(int i = 0; i < triangles.length; i++) {
            Triangle triangle = triangles[i];

            Vertex[] vertices = triangle.getVertices();
            unpackedVertices[i * 3] = vertices[0];
            unpackedVertices[i * 3 + 1] = vertices[1];
            unpackedVertices[i * 3 + 2] = vertices[2];

            Vector3f normal = triangle.getNormal();
            unpackedNormals[i * 3] = normal;
            unpackedNormals[i * 3 + 1] = normal;
            unpackedNormals[i * 3 + 2] = normal;

            Vector3f tangent = triangle.getTangent();
            unpackedTangents[i * 3] = tangent;
            unpackedTangents[i * 3 + 1] = tangent;
            unpackedTangents[i * 3 + 2] = tangent;
        }

        // Generate indices and packed data from unpacked data:

        int[] indices = new int[unpackedVertices.length];
        List<Vertex> packedVertices = new ArrayList<>(unpackedVertices.length);
        List<Vector3f> packedNormals = new ArrayList<>(unpackedVertices.length);
        List<Vector3f> packedTangents = new ArrayList<>(unpackedVertices.length);

        for(int i = 0; i < unpackedVertices.length; i++) {
            Vertex vertex = unpackedVertices[i];
            Vector3f normal = unpackedNormals[i];
            Vector3f tangent = unpackedTangents[i];

            int foundIndex = -1;
            for(int j = 0; j < packedVertices.size(); j++) {
                if(!vertex.equals(packedVertices.get(j))) continue;
                if(!shadeSmooth && !normal.equals(packedNormals.get(j))) continue;
                foundIndex = j;
                break;
            }

            if(foundIndex == -1) {
                indices[i] = packedVertices.size();

                packedVertices.add(vertex);
                packedNormals.add(normal);
                packedTangents.add(tangent);
            } else {
                indices[i] = foundIndex;

                // Only sum when smooth shading is enabled, otherwise it's pointless:
                if(shadeSmooth) packedNormals.set(foundIndex, packedNormals.get(foundIndex).add(normal));
                packedTangents.set(foundIndex, packedTangents.get(foundIndex).add(tangent));
            }
        }

        // Normalize vectors and store vertices in a float array:

        final int vertexFloats = 3 + 2 + 3 + 3;
        float[] vertices = new float[packedVertices.size() * vertexFloats];

        for(int i = 0; i < packedVertices.size(); i++) {
            Vertex vertex = packedVertices.get(i);
            Vector3f normal = packedNormals.get(i).normalize();
            Vector3f tangent = packedTangents.get(i).normalize();

            System.arraycopy(vertex.toArray(), 0, vertices, i * vertexFloats, 5);
            System.arraycopy(normal.toArray(), 0, vertices, i * vertexFloats + 5, 3);
            System.arraycopy(tangent.toArray(), 0, vertices, i * vertexFloats + 8, 3);
        }

        return new GeometryData(vertices, indices);
    }
}
