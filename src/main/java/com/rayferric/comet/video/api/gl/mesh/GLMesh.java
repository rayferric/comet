package com.rayferric.comet.video.api.gl.mesh;

import com.rayferric.comet.server.ServerResource;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLMesh implements ServerResource {
    public GLMesh(FloatBuffer vertices, IntBuffer indices) {
        System.out.println("Creating GLMesh...");

        vertexArray = glGenVertexArrays();
        glGenBuffers(vertexBuffers);
        glBindVertexArray(vertexArray);
        indexCount = indices.capacity();

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffers[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * 11, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 3);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 5);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, Float.BYTES * 11, Float.BYTES * 8);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vertexBuffers[1]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    @Override
    public void destroy() {
        System.out.println("Destroying GLMesh...");
        glDeleteBuffers(vertexBuffers);
        glDeleteVertexArrays(vertexArray);
    }

    public void bind() {
        glBindVertexArray(vertexArray);
    }

    public int getIndexCount() {
        return indexCount;
    }

    private final int vertexArray;
    private final int[] vertexBuffers = new int[2];
    private final int indexCount;
}
