package com.rayferric.comet.math;

public class Vector2i {
    public Vector2i() {
        x = y = 0;
    }

    public Vector2i(int x) {
        this.x = this.y = x;
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
    public String toString() {
        return String.format("Vector2i{x=%s, y=%s}", x, y);
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

    private int x, y;
}
