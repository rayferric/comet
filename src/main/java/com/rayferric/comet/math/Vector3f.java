package com.rayferric.comet.math;

import java.util.Objects;

public class Vector3f {
    public static final int BYTES = 12;

    public Vector3f() {
        x = y = z = 0;
    }

    public Vector3f(float all) {
        x = y = z = all;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public Vector3f(Vector3f other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector3f other = (Vector3f)o;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vector3f{x=%s, y=%s, z=%s}", x, y, z);
    }

    public float[] toArray() {
        return new float[] { x, y, z };
    }

    public Vector3f add(Vector3f rhs) {
        return new Vector3f(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vector3f sub(Vector3f rhs) {
        return new Vector3f(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vector3f mul(Vector3f rhs) {
        return new Vector3f(x * rhs.x, y * rhs.y, z * rhs.z);
    }

    public Vector3f mul(float rhs) {
        return new Vector3f(x * rhs, y * rhs, z * rhs);
    }

    public Vector3f div(Vector3f rhs) {
        return new Vector3f(x / rhs.x, y / rhs.y, z / rhs.z);
    }

    public Vector3f div(float rhs) {
        return new Vector3f(x / rhs, y / rhs, z / rhs);
    }

    public float dot(Vector3f rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    public Vector3f cross(Vector3f rhs) {
        return new Vector3f(
                (y * rhs.z) - (rhs.y * z),
                (z * rhs.x) - (rhs.z * x),
                (x * rhs.y) - (rhs.x * y)
        );
    }

    public float length() {
        return Mathf.sqrt(dot(this));
    }

    public Vector3f normalize() {
        float length = length();
        return length == 0 ? new Vector3f(0) : this.div(length);
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

    private float x, y, z;
}
