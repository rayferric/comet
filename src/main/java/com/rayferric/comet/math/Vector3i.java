package com.rayferric.comet.math;

import java.util.Objects;

public class Vector3i {
    public static final int BYTES = 12;

    public Vector3i() {
        x = y = z = 0;
    }

    public Vector3i(int all) {
        x = y = z = all;
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i(Vector3i other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector3i other = (Vector3i)o;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("Vector3i{x=%s, y=%s, z=%s}", x, y, z);
    }

    public Vector3i add(Vector3i rhs) {
        return new Vector3i(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vector3i sub(Vector3i rhs) {
        return new Vector3i(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vector3i mul(Vector3i rhs) {
        return new Vector3i(x * rhs.x, y * rhs.y, z * rhs.z);
    }

    public Vector3i mul(int rhs) {
        return new Vector3i(x * rhs, y * rhs, z * rhs);
    }

    public Vector3i div(Vector3i rhs) {
        return new Vector3i(x / rhs.x, y / rhs.y, z / rhs.z);
    }

    public Vector3i div(int rhs) {
        return new Vector3i(x / rhs, y / rhs, z / rhs);
    }

    public int[] toArray() {
        return new int[] { x, y, z };
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    private int x, y, z;
}
