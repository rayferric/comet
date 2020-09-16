package com.rayferric.comet.core.mesh;

import com.rayferric.comet.core.math.Vector2f;
import com.rayferric.comet.core.math.Vector3f;

public class Triangle {
    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;

        Vector3f p1 = v1.getPosition();
        Vector3f p2 = v2.getPosition();
        Vector3f p3 = v3.getPosition();

        Vector3f deltaPos1 = p2.sub(p1);
        Vector3f deltaPos2 = p3.sub(p1);

        normal = deltaPos1.cross(deltaPos2);

        Vector2f t1 = v1.getTexCoord();
        Vector2f t2 = v2.getTexCoord();
        Vector2f t3 = v3.getTexCoord();

        Vector2f deltaUv1 = t2.sub(t1);
        Vector2f deltaUv2 = t3.sub(t1);

        float num = 1 / (deltaUv1.getX() * deltaUv2.getY() - deltaUv1.getY() * deltaUv2.getX());
        tangent = deltaPos1.mul(deltaUv2.getY()).sub(deltaPos2.mul(deltaUv1.getY())).mul(num);
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
}
