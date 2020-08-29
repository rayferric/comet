package com.rayferric.comet.math;

import java.util.Objects;

public class Vector4i {
    public static final int BYTES = 16;

    public Vector4i() {
        x = y = z = w = 0;
    }

    public Vector4i(int all) {
        x = y = z = w = all;
    }

    public Vector4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4i(Vector4i other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector4i other = (Vector4i)o;
        return x == other.x && y == other.y && z == other.z && w == other.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return String.format("Vector4i{x=%s, y=%s, z=%s, w=%s}", x, y, z, w);
    }

    public Vector4i add(Vector4i rhs) {
        return new Vector4i(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w);
    }

    public Vector4i sub(Vector4i rhs) {
        return new Vector4i(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w);
    }

    public Vector4i mul(Vector4i rhs) {
        return new Vector4i(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w);
    }

    public Vector4i mul(int rhs) {
        return new Vector4i(x * rhs, y * rhs, z * rhs, w * rhs);
    }

    public Vector4i div(Vector4i rhs) {
        return new Vector4i(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w);
    }

    public Vector4i div(int rhs) {
        return new Vector4i(x / rhs, y / rhs, z / rhs, w / rhs);
    }

    public int[] toArray() {
        return new int[] { x, y, z, w };
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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    private int x, y, z, w;
}
