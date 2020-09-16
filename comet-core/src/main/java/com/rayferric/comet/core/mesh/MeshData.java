package com.rayferric.comet.core.mesh;

import com.rayferric.comet.core.math.AABB;
import com.rayferric.comet.core.math.Vector3f;

import java.util.Arrays;

public class MeshData {
    public MeshData(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.aabb = computeAabb();
    }

    @Override
    public String toString() {
        return String.format("MeshData{vertices=%s, indices=%s}", Arrays.toString(vertices),
                Arrays.toString(indices));
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public AABB getAabb() {
        return aabb;
    }

    private final float[] vertices;
    private final int[] indices;
    private final AABB aabb;

    private AABB computeAabb() {
        float minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        for(int i = 0; i < vertices.length; i += 11) {
            float x = vertices[i];
            if(x < minX) minX = x;
            else if(x > maxX) maxX = x;

            float y = vertices[i + 1];
            if(y < minY) minY = y;
            else if(y > maxY) maxY = y;

            float z = vertices[i + 2];
            if(z < minZ) minZ = z;
            else if(z > maxZ) maxZ = z;
        }

        return new AABB(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
    }
}
