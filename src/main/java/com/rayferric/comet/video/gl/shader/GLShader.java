package com.rayferric.comet.video.gl.shader;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.glProgramUniform4f;
import static org.lwjgl.opengl.GL45.*;

public abstract class GLShader implements ServerResource {
    @Override
    public void destroy() {
        System.out.println("Destroying GLShader...");
        glDeleteProgram(handle);
    }

    public void bind() {
        glUseProgram(handle);
    }

    public void setUniform(int location, int value) {
        glProgramUniform1i(handle, location, value);
    }

    public void setUniform(int location, double value) {
        glProgramUniform1f(handle, location, (float)value);
    }

    public void setUniform(int location, Vector3f value) {
        glProgramUniform3f(handle, location, value.getX(), value.getY(), value.getZ());
    }

    public void setUniform(int location, Vector4f value) {
        glProgramUniform4f(handle, location, value.getX(), value.getY(), value.getZ(), value.getW());
    }

    public void setUniform(int location, Matrix4f value) {
        glProgramUniformMatrix4fv(handle, location, false, value.toArray());
    }

    public void setUniform(int location, int[] values) {
        glProgramUniform1iv(handle, location, values);
    }

    public void setUniform(int location, float[] values) {
        glProgramUniform1fv(handle, location, values);
    }

    public void setUniform(int location, Vector3f[] values) {
        float[] array = new float[values.length * 3];
        for(int i = 0; i < values.length; i++)
            System.arraycopy(values[i].toArray(), 0, array, i * 3, 3);

        glProgramUniform3fv(handle, location, array);
    }

    public void setUniform(int location, Vector4f[] values) {
        float[] array = new float[values.length * 4];
        for(int i = 0; i < values.length; i++)
            System.arraycopy(values[i].toArray(), 0, array, i * 4, 4);

        glProgramUniform4fv(handle, location, array);
    }

    public void setUniform(int location, Matrix4f[] values) {
        float[] array = new float[values.length * 16];
        for(int i = 0; i < values.length; i++)
            System.arraycopy(values[i].toArray(), 0, array, i * 16, 16);

        glProgramUniformMatrix4fv(handle, location, false, array);
    }

    public int getHandle() {
        return handle;
    }

    protected void link(int vertShader, int fragShader) {
        System.out.println("Creating GLShader...");

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
