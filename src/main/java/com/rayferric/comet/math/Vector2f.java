package com.rayferric.comet.math;

import java.util.Objects;

public class Vector2f {
    public static final int BYTES = 8;

    public static final Vector2f ZERO = new Vector2f(0);
    public static final Vector2f LEFT = new Vector2f(-1, 0);
    public static final Vector2f RIGHT = new Vector2f(1, 0);
    public static final Vector2f DOWN = new Vector2f(0, -1);
    public static final Vector2f UP = new Vector2f(0, 1);

    public Vector2f() {
        x = y = 0;
    }

    public Vector2f(float all) {
        x = y = all;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(double x, double y) {
        this.x = (float)x;
        this.y = (float)y;
    }

    public Vector2f(Vector2f other) {
        x = other.x;
        y = other.y;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector2f other = (Vector2f)o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Vector2f{x=%s, y=%s}", x, y);
    }

    public float[] toArray() {
        return new float[] { x, y };
    }

    public Vector2f add(Vector2f rhs) {
        return new Vector2f(x + rhs.x, y + rhs.y);
    }

    public Vector2f sub(Vector2f rhs) {
        return new Vector2f(x - rhs.x, y - rhs.y);
    }

    public Vector2f mul(Vector2f rhs) {
        return new Vector2f(x * rhs.x, y * rhs.y);
    }

    public Vector2f mul(float rhs) {
        return new Vector2f(x * rhs, y * rhs);
    }

    public Vector2f div(Vector2f rhs) {
        return new Vector2f(x / rhs.x, y / rhs.y);
    }

    public Vector2f div(float rhs) {
        return new Vector2f(x / rhs, y / rhs);
    }

    public float dot(Vector2f rhs) {
        return x * rhs.x + y * rhs.y;
    }

    public float length() {
        return Mathf.sqrt(dot(this));
    }

    public float distance(Vector2f rhs) {
        return sub(rhs).length();
    }

    public Vector2f normalize() {
        float length = length();
        return length == 0 ? new Vector2f(0) : this.div(length);
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

    private float x, y;
}
