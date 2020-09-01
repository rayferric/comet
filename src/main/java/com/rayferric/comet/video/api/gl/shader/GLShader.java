package com.rayferric.comet.video.api.gl.shader;

import com.rayferric.comet.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

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

        if(vertShader != 0) glAttachShader(handle, vertShader);
        if(fragShader != 0) glAttachShader(handle, fragShader);

        glLinkProgram(handle);

        if(vertShader != 0) glDetachShader(handle, vertShader);
        if(fragShader != 0) glDetachShader(handle, fragShader);

        if(vertShader != 0) glDeleteShader(vertShader);
        if(fragShader != 0) glDeleteShader(fragShader);

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
