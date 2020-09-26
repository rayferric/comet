package com.rayferric.comet.shapeconverter.shape;

import com.rayferric.comet.shapeconverter.math.Matrix4f;
import com.rayferric.comet.shapeconverter.math.Vector3f;

import java.util.List;

public class Shape {
    public static final byte TYPE_CONCAVE = 0;
    public static final byte TYPE_CONVEX = 1;

    public Shape(Matrix4f transform, List<Vector3f> positions) {
        this.transform = transform;
        this.positions = positions;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public List<Vector3f>  getPositions() {
        return positions;
    }

    private final Matrix4f transform;
    private final List<Vector3f> positions;
}
