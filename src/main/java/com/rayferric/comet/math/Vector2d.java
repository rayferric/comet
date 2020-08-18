package com.rayferric.comet.math;

import java.util.Objects;

public class Vector2d {
    public Vector2d() {
        x = y = 0;
    }

    public Vector2d(double all) {
        x = y = all;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2d other) {
        x = other.x;
        y = other.y;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector2d other = (Vector2d)o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Vector2d{x=%s, y=%s}", x, y);
    }

    public Vector2d add(Vector2d rhs) {
        return new Vector2d(x + rhs.x, y + rhs.y);
    }

    public Vector2d sub(Vector2d rhs) {
        return new Vector2d(x - rhs.x, y - rhs.y);
    }

    public Vector2d mul(Vector2d rhs) {
        return new Vector2d(x * rhs.x, y * rhs.y);
    }

    public Vector2d mul(double rhs) {
        return new Vector2d(x * rhs, y * rhs);
    }

    public Vector2d div(Vector2d rhs) {
        return new Vector2d(x / rhs.x, y / rhs.y);
    }

    public Vector2d div(double rhs) {
        return new Vector2d(x / rhs, y / rhs);
    }

    public static double dot(Vector2d lhs, Vector2d rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y;
    }

    public double length() {
        return Math.sqrt(dot(this, this));
    }

    public Vector2d normalize() {
        double length = length();
        return length == 0.0 ? new Vector2d(0.0) : this.div(length);
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

    private double x, y;
}
