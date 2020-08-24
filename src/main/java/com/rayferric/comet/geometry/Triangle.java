package com.rayferric.comet.geometry;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;

public class Triangle {
    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;

        normal = computeNormal();
        tangent = computeTangent();
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector3f getTangent() {
        return tangent;
    }

    private final Vertex[] vertices = new Vertex[3];
    private final Vector3f normal, tangent;

    private Vector3f computeNormal() {
        Vector3f p1 = vertices[0].getPosition();
        Vector3f p2 = vertices[1].getPosition();
        Vector3f p3 = vertices[2].getPosition();

        Vector3f deltaPos1 = p2.sub(p1);
        Vector3f deltaPos2 = p3.sub(p1);

        return Vector3f.cross(deltaPos1, deltaPos2);
    }

    private Vector3f computeTangent() {
        Vector3f p1 = vertices[0].getPosition();
        Vector3f p2 = vertices[1].getPosition();
        Vector3f p3 = vertices[2].getPosition();

        Vector2f t1 = vertices[0].getTexCoord();
        Vector2f t2 = vertices[1].getTexCoord();
        Vector2f t3 = vertices[2].getTexCoord();

        Vector3f deltaPos1 = p2.sub(p1);
        Vector3f deltaPos2 = p3.sub(p1);

        Vector2f deltaUv1 = t2.sub(t1);
        Vector2f deltaUv2 = t3.sub(t1);

        float num = 1 / (deltaUv1.getX() * deltaUv2.getY() - deltaUv1.getY() * deltaUv2.getX());
        return deltaPos1.mul(deltaUv2.getY()).sub(deltaPos2.mul(deltaUv1.getY())).mul(num);
    }
}
