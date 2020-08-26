package com.rayferric.comet.video.api.gl.shader;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.glProgramUniform4f;
import static org.lwjgl.opengl.GL45.*;

public abstract class GLShader implements ServerResource {
    @Override
    public void destroy() {
        glDeleteProgram(handle);
    }

    public void bind() {
        glUseProgram(handle);
    }

    public int getHandle() {
        return handle;
    }

    protected void link(int vertShader, int fragShader) {
        handle = glCreateProgram();

        glAttachShader(handle, vertShader);
        glAttachShader(handle, fragShader);
        glLinkProgram(handle);
        glDetachShader(handle, vertShader);
        glDetachShader(handle, fragShader);

        glDeleteShader(vertShader);
        glDeleteShader(fragShader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer status = stack.mallocInt(1);
            glGetProgramiv(handle, GL_LINK_STATUS, status);
            if(status.get(0) == GL_FALSE) {
                String info = glGetProgramInfoLog(handle);
                glDeleteProgram(handle);
                throw new RuntimeException("Failed to link shader program.\n" + info);
            }
        }

        glValidateProgram(handle);
    }

    private int handle;
}
