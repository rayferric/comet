package com.rayferric.comet.video.api.gl.shader;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBGLSPIRV.*;
import static org.lwjgl.opengl.GL45.*;

public class GLBinaryShader extends GLShader {
    public GLBinaryShader(ByteBuffer vertBin, ByteBuffer fragBin) {
        int vertShader = createShader(GL_VERTEX_SHADER, vertBin);
        int fragShader = createShader(GL_FRAGMENT_SHADER, fragBin);
        link(vertShader, fragShader);
    }

    private int createShader(int type, ByteBuffer binary) {
        if(binary == null) return 0;

        int shader = glCreateShader(type);
        glShaderBinary(new int[] { shader }, GL_SHADER_BINARY_FORMAT_SPIR_V_ARB, binary);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            glSpecializeShaderARB(shader, "main", stack.mallocInt(0), stack.mallocInt(0));

            IntBuffer status = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, status);
            if(status.get(0) == GL_FALSE) {
                String info = glGetShaderInfoLog(shader);
                throw new RuntimeException("Failed to specialize shader binary.\n" + info);
            }
        }

        return shader;
    }
}
