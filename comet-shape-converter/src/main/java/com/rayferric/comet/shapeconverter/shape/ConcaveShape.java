package com.rayferric.comet.shapeconverter.shape;

import com.rayferric.comet.shapeconverter.math.Matrix4f;
import com.rayferric.comet.shapeconverter.math.Vector3f;
import com.rayferric.comet.shapeconverter.math.Vector3i;

import java.util.List;

public class ConcaveShape extends Shape {
    public ConcaveShape(Matrix4f transform, List<Vector3f>  positions, List<Vector3i> triangles) {
        super(transform, positions);
        this.triangles = triangles;
    }

    public List<Vector3i> getTriangles() {
        return triangles;
    }

    private final List<Vector3i> triangles;
}
