package com.rayferric.comet.core.video.api.gl.buffer;

import com.rayferric.comet.core.server.ServerResource;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLShaderStorageBuffer implements ServerResource {
    public GLShaderStorageBuffer(int size) {
        handle = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, handle);
        glBufferData(GL_SHADER_STORAGE_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public void destroy() {
        glDeleteBuffers(handle);
    }

    public void bind(int binding) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, handle);
    }

    public void update(FloatBuffer data) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, handle);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, data);
    }

    private final int handle;
}
