package com.rayferric.comet.geometry;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;

import java.util.Objects;

public class Vertex {
    public static final int BYTES = 20;

    public Vertex(float px, float py, float pz, float tx, float ty) {
        this.position = new Vector3f(px, py, pz);
        this.texCoord = new Vector2f(tx, ty);
    }

    public Vertex(Vector3f position, Vector2f texCoord, Vector3f normal) {
        this.position = position;
        this.texCoord = texCoord;
    }

    public Vertex(Vertex other) {
        this.position = other.position;
        this.texCoord = other.texCoord;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Vertex other = (Vertex)o;
        return Objects.equals(position, other.position) &&
                Objects.equals(texCoord, other.texCoord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, texCoord);
    }

    @Override
    public String toString() {
        return String.format("Vertex{position=%s, texCoord=%s}", position, texCoord);
    }

    public float[] toArray() {
        float[] array = new float[3 + 2];

        System.arraycopy(position.toArray(), 0, array, 0, 3);
        System.arraycopy(texCoord.toArray(), 0, array, 3, 2);

        return array;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector2f getTexCoord() {
        return texCoord;
    }

    public void setTexCoord(Vector2f texCoord) {
        this.texCoord = texCoord;
    }

    private Vector3f position;
    private Vector2f texCoord;
}
