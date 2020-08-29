package com.rayferric.comet.geometry;

import java.util.Arrays;

public class GeometryData {
    public GeometryData() {
        this.vertices = new float[0];
        this.indices = new int[0];
    }

    public GeometryData(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    @Override
    public String toString() {
        return String.format("GeometryData{vertices=%s, indices=%s}", Arrays.toString(vertices),
                Arrays.toString(indices));
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    private final float[] vertices;
    private final int[] indices;
}
