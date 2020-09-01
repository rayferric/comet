package com.rayferric.comet.math;

public class AABB {
    public AABB() {
        this.min = new Vector3f(0);
        this.max = new Vector3f(0);
    }

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public Vector3f getOrigin(Matrix4f transform) {
        return transform.mul(min, 1).add(transform.mul(max, 1)).mul(0.5F);
    }

    public float getBoundingRadius(Matrix4f transform) {
        return transform.mul(min, 1).distance(transform.mul(max, 1)) * 0.5F;
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public Vector3f getMax() {
        return max;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    private Vector3f min, max;
}
