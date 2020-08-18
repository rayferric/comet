package com.rayferric.comet.math;

import java.util.Objects;

public class Vector4d {
    public Vector4d() {
        x = y = z = w = 0;
    }

    public Vector4d(double all) {
        x = y = z = w = all;
    }

    public Vector4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4d(Vector4d other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector4d other = (Vector4d)o;
        return x == other.x && y == other.y && z == other.z && w == other.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return String.format("Vector4d{x=%s, y=%s, z=%s, w=%s}", x, y, z, w);
    }

    public Vector4d add(Vector4d rhs) {
        return new Vector4d(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w);
    }

    public Vector4d sub(Vector4d rhs) {
        return new Vector4d(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w);
    }

    public Vector4d mul(Vector4d rhs) {
        return new Vector4d(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w);
    }

    public Vector4d mul(double rhs) {
        return new Vector4d(x * rhs, y * rhs, z * rhs, w * rhs);
    }

    public Vector4d div(Vector4d rhs) {
        return new Vector4d(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w);
    }

    public Vector4d div(double rhs) {
        return new Vector4d(x / rhs, y / rhs, z / rhs, w / rhs);
    }

    public static double dot(Vector4d lhs, Vector4d rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z + lhs.w * rhs.w;
    }

    public double length() {
        return Math.sqrt(dot(this, this));
    }

    public Vector4d normalize() {
        double length = length();
        return length == 0.0 ? new Vector4d(0.0) : this.div(length);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    private double x, y, z, w;
}
