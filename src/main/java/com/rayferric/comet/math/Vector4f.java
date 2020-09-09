package com.rayferric.comet.math;

import java.util.Objects;

public class Vector4f {
    public static final int BYTES = 16;

    public static final Vector4f ZERO = new Vector4f(0);
    public static final Vector4f ONE = new Vector4f(1);

    public static final Vector4f LEFT = new Vector4f(-1, 0, 0, 0);
    public static final Vector4f RIGHT = new Vector4f(1, 0, 0, 0);
    public static final Vector4f DOWN = new Vector4f(0, -1, 0, 0);
    public static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    public static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    public static final Vector4f BACKWARD = new Vector4f(0, 0, 1, 0);
    public static final Vector4f PAST = new Vector4f(0, 0, 0, -1);
    public static final Vector4f FUTURE = new Vector4f(0, 0, 0, 1);

    public Vector4f() {
        x = y = z = w = 0;
    }

    public Vector4f(float all) {
        x = y = z = w = all;
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(double x, double y, double z, double w) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        this.w = (float)w;
    }

    public Vector4f(Vector4f other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector4f other = (Vector4f)o;
        return x == other.x && y == other.y && z == other.z && w == other.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return String.format("Vector4f{x=%s, y=%s, z=%s, w=%s}", x, y, z, w);
    }

    public float[] toArray() {
        return new float[] { x, y, z, w };
    }

    public Vector4f add(Vector4f rhs) {
        return new Vector4f(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w);
    }

    public Vector4f sub(Vector4f rhs) {
        return new Vector4f(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w);
    }

    public Vector4f mul(Vector4f rhs) {
        return new Vector4f(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w);
    }

    public Vector4f mul(float rhs) {
        return new Vector4f(x * rhs, y * rhs, z * rhs, w * rhs);
    }

    public Vector4f div(Vector4f rhs) {
        return new Vector4f(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w);
    }

    public Vector4f div(float rhs) {
        return new Vector4f(x / rhs, y / rhs, z / rhs, w / rhs);
    }

    public float dot(Vector4f rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z + w * rhs.w;
    }

    public float length() {
        return Mathf.sqrt(dot(this));
    }

    public float distance(Vector4f rhs) {
        return sub(rhs).length();
    }

    public Vector4f normalize() {
        float length = length();
        return length == 0 ? new Vector4f(0) : this.div(length);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    private float x, y, z, w;
}
