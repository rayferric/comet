package com.rayferric.comet.math;

import java.util.Objects;

public class Vector2f {
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
        return String.format("Vector2d{x=%s, y=%s}", x, y);
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

    public static float dot(Vector2f lhs, Vector2f rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y;
    }

    public float length() {
        return (float)Math.sqrt(dot(this, this));
    }

    public Vector2f normalize() {
        float length = length();
        return length == 0 ? new Vector2f(0) : this.div(length);
    }

    public float[] toArray() {
        return new float[]{x, y};
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
