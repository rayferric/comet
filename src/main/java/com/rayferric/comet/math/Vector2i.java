package com.rayferric.comet.math;

import java.util.Objects;

public class Vector2i {
    public static final int BYTES = 8;

    public Vector2i() {
        x = y = 0;
    }

    public Vector2i(int all) {
        x = y = all;
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(Vector2i other) {
        x = other.x;
        y = other.y;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vector2i other = (Vector2i)o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Vector2i{x=%s, y=%s}", x, y);
    }

    public int[] toArray() {
        return new int[] { x, y };
    }

    public Vector2i add(Vector2i rhs) {
        return new Vector2i(x + rhs.x, y + rhs.y);
    }

    public Vector2i sub(Vector2i rhs) {
        return new Vector2i(x - rhs.x, y - rhs.y);
    }

    public Vector2i mul(Vector2i rhs) {
        return new Vector2i(x * rhs.x, y * rhs.y);
    }

    public Vector2i mul(int rhs) {
        return new Vector2i(x * rhs, y * rhs);
    }

    public Vector2i div(Vector2i rhs) {
        return new Vector2i(x / rhs.x, y / rhs.y);
    }

    public Vector2i div(int rhs) {
        return new Vector2i(x / rhs, y / rhs);
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

    private int x, y;
}
