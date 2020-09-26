package com.rayferric.comet.core.shape;

public class ShapeData {
    public ShapeData(float[] positions, int[] triangles) {
        this.positions = positions;
        this.triangles = triangles;
    }

    public float[] getPositions() {
        return positions;
    }

    public int[] getTriangles() {
        return triangles;
    }

    private final float[] positions;
    private final int[] triangles;
}
