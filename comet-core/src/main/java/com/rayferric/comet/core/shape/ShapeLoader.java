package com.rayferric.comet.core.shape;

import com.rayferric.comet.core.util.ResourceLoader;

import java.nio.ByteBuffer;

public class ShapeLoader {
    public static ShapeData load(boolean fromJar, String path) {
        ByteBuffer buffer = ResourceLoader.readBinaryFile(fromJar, path);

        int numPositions = buffer.getInt();
        int numTriangles = buffer.getInt();

        float[] positions = new float[numPositions * 3];
        int[] triangles = new int[numTriangles * 3];

        for(int i = 0; i < numPositions; i++)
            positions[i] = buffer.getFloat();
        for(int i = 0; i < numTriangles; i++)
            triangles[i] = buffer.getInt();

        return new ShapeData(positions, triangles);
    }
}
