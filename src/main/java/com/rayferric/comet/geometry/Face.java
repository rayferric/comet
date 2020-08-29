package com.rayferric.comet.geometry;

import java.util.Arrays;

public class Face {
    public Face(Vertex[] vertices) {
        this.vertices = vertices;
    }

    @Override
    public String toString() {
        return String.format("Face{vertices=%s}", Arrays.toString(vertices));
    }

    public int getTriangleCount() {
        return Math.max(vertices.length - 2, 0);
    }

    public Triangle[] triangulate() {
        int triangleCount = getTriangleCount();
        if(triangleCount == 0) return null;

        Triangle[] triangles = new Triangle[triangleCount];

        Vertex first = vertices[0];
        for(int i = 1; i < vertices.length - 1; i++) {
            Vertex second = vertices[i];
            Vertex third = vertices[i + 1];

            triangles[i - 1] = new Triangle(first, second, third);
        }

        return triangles;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    private final Vertex[] vertices;
}
