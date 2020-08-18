package com.rayferric.comet.math;

import java.util.Objects;

public class Vector3d {
    public Vector3d() {
        x = y = z = 0;
    }

    public Vector3d(double all) {
        x = y = z = all;
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vector3d other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector3d other = (Vector3d)o;
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

    public Vector3d add(Vector3d rhs) {
        return new Vector3d(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vector3d sub(Vector3d rhs) {
        return new Vector3d(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vector3d mul(Vector3d rhs) {
        return new Vector3d(x * rhs.x, y * rhs.y, z * rhs.z);
    }

    public Vector3d mul(double rhs) {
        return new Vector3d(x * rhs, y * rhs, z * rhs);
    }

    public Vector3d div(Vector3d rhs) {
        return new Vector3d(x / rhs.x, y / rhs.y, z / rhs.z);
    }

    public Vector3d div(double rhs) {
        return new Vector3d(x / rhs, y / rhs, z / rhs);
    }

    public static double dot(Vector3d lhs, Vector3d rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z;
    }

    public static Vector3d cross(Vector3d lhs, Vector3d rhs) {
        return new Vector3d(
                (lhs.y * rhs.z) - (rhs.y * lhs.z),
                (lhs.z * rhs.x) - (rhs.z * lhs.x),
                (lhs.x * rhs.y) - (rhs.x * lhs.y)
        );
    }

    public double length() {
        return Math.sqrt(dot(this, this));
    }

    public Vector3d normalize() {
        double length = length();
        return length == 0.0 ? new Vector3d(0.0) : this.div(length);
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

    private double x, y, z;
}
