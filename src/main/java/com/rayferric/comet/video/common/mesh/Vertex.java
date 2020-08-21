package com.rayferric.comet.video.common.mesh;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;

/**
 * Encapsulates vertex data.
 */
public class Vertex {
    /**
     * Creates a vertex.
     *
     * @param position position
     * @param texCoord texture coordinate
     * @param normal   normal
     * @param tangent  tangent
     */
    public Vertex(Vector3f position, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
        this.position = position;
        this.texCoord = texCoord;
        this.normal = normal;
        this.tangent = tangent;
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

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getTangent() {
        return tangent;
    }

    public void setTangent(Vector3f tangent) {
        this.tangent = tangent;
    }

    private Vector3f position;
    private Vector2f texCoord;
    private Vector3f normal;
    private Vector3f tangent;
}
