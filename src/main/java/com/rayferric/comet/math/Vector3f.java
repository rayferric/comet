package com.rayferric.comet.math;

import java.util.Objects;

public class Vector3f {
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
        return String.format("Vector3d{x=%s, y=%s, z=%s}", x, y, z);
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

    public static float dot(Vector3f lhs, Vector3f rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z;
    }

    public static Vector3f cross(Vector3f lhs, Vector3f rhs) {
        return new Vector3f(
                (lhs.y * rhs.z) - (rhs.y * lhs.z),
                (lhs.z * rhs.x) - (rhs.z * lhs.x),
                (lhs.x * rhs.y) - (rhs.x * lhs.y)
        );
    }

    public float length() {
        return (float)Math.sqrt(dot(this, this));
    }

    public Vector3f normalize() {
        float length = length();
        return length == 0 ? new Vector3f(0) : this.div(length);
    }

    public float[] toArray() {
        return new float[] { x, y, z };
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
