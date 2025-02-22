package com.rayferric.comet.core.video.api.gl.mesh;

import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.math.AABB;
import com.rayferric.comet.core.server.ServerResource;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLMesh implements ServerResource {
    public GLMesh(MeshData data) {
        float[] vertices = data.getVertices();
        int[] indices = data.getIndices();

        vertexCount = vertices.length / 11;
        indexCount = indices.length;

        aabb = data.getAabb();

        vertexArray = glGenVertexArrays();
        glGenBuffers(vertexBuffers);
        glBindVertexArray(vertexArray);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffers[0]);
        FloatBuffer nativeVertices = MemoryUtil.memAllocFloat(vertices.length);
        glBufferData(GL_ARRAY_BUFFER, nativeVertices.put(vertices).flip(), GL_STATIC_DRAW);
        MemoryUtil.memFree(nativeVertices);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * 11, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 3);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 5);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 8);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
        IntBuffer nativeIndices = MemoryUtil.memAllocInt(indices.length);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, nativeIndices.put(indices).flip(), GL_STATIC_DRAW);
        MemoryUtil.memFree(nativeIndices);
    }

    @Override
    public void destroy() {
        glDeleteBuffers(vertexBuffers);
        glDeleteVertexArrays(vertexArray);
    }

    public void bind() {
        glBindVertexArray(vertexArray);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public AABB getAabb() {
        return aabb;
    }

    private final int vertexArray;
    private final int[] vertexBuffers = new int[2];
    private final int vertexCount, indexCount;
    private final AABB aabb;
}
