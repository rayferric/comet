package com.rayferric.comet.video.api.gl.buffer;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLUniformBuffer implements ServerResource {
    public GLUniformBuffer(int size) {
        System.out.println("Creating GLUniformBuffer...");
        handle = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, handle);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public void destroy() {
        System.out.println("Destroying GLUniformBuffer...");
        glDeleteBuffers(handle);
    }

    public void bind(int binding) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle);
        glBindBufferBase(GL_UNIFORM_BUFFER, binding, handle);
    }

    public void nativeUpdate(FloatBuffer nativeData) {
        glBindBuffer(GL_UNIFORM_BUFFER, handle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, nativeData);
    }

    private final int handle;
}
