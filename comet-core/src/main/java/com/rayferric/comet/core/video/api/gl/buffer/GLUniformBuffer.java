package com.rayferric.comet.core.video.api.gl.buffer;

import com.rayferric.comet.core.server.ServerResource;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLUniformBuffer implements ServerResource {
    public GLUniformBuffer(int size) {
        handle = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, handle);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public void destroy() {
        glDeleteBuffers(handle);
    }

    public void bind(int binding) {
        glBindBufferBase(GL_UNIFORM_BUFFER, binding, handle);
    }

    public void update(FloatBuffer data) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, data);
    }

    public boolean popJustCreated() {
        if(justCreated) {
            justCreated = false;
            return true;
        } else return false;
    }

    private final int handle;
    private boolean justCreated = true;
}
