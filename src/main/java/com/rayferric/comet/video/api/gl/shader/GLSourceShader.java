package com.rayferric.comet.video.api.gl.shader;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class GLSourceShader extends GLShader {
    public GLSourceShader(String vertSrc, String fragSrc) {
        int vertShader = createShader(GL_VERTEX_SHADER, vertSrc);
        int fragShader = createShader(GL_FRAGMENT_SHADER, fragSrc);
        link(vertShader, fragShader);
    }

    private int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            glCompileShader(shader);

            IntBuffer status = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, status);
            if(status.get(0) == GL_FALSE) {
                String info = glGetShaderInfoLog(shader);
                throw new RuntimeException("Failed to compile shader source.\n" + info);
            }
        }

        return shader;
    }
}
