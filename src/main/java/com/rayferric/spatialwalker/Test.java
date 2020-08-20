package com.rayferric.spatialwalker;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBGLSPIRV.GL_SHADER_BINARY_FORMAT_SPIR_V_ARB;
import static org.lwjgl.opengl.ARBGLSPIRV.glSpecializeShaderARB;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL41.glShaderBinary;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Test {
    public static void main(String[] args) {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        long window = glfwCreateWindow(800, 600, "Test", NULL, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create glfw window");
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        byte[] data = readBinaryFile("shader.vert.spv");

        int shader = glCreateShader(GL_VERTEX_SHADER);
        System.out.println("Shader created. " + glGetError());

        try(MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buf = stack.malloc(data.length);
            buf.put(data);
            buf.flip();
            glShaderBinary(new int[]{shader}, GL_SHADER_BINARY_FORMAT_SPIR_V_ARB, buf);
            System.out.println("Binary loaded. " + glGetError());

            glSpecializeShaderARB(shader, "main", stack.mallocInt(0), stack.mallocInt(0));

            System.out.println("Binary specialized. " + glGetError());
        }

        while(!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static byte[] readBinaryFile(String path) {
        File file = new File(path);
        byte[] buffer = new byte[(int)file.length()];

        try {
            InputStream stream = new FileInputStream(file);
            stream.read(buffer);
            stream.close();
        } catch(Exception e) {
            throw new RuntimeException(String.format("Failed to read file.\n%s\n%s", path, e.getMessage()));
        }

        return buffer;
    }

    private static ByteBuffer readBinaryFileToNativeBuffer(String path) {
        File file = new File(path);
        ByteBuffer buffer = ByteBuffer.allocate((int)file.length());

        try {
            InputStream stream = new FileInputStream(file);
            stream.read(buffer.array());
            stream.close();
        } catch(Exception e) {
            throw new RuntimeException(String.format("Failed to read file.\n%s\n%s", path, e.getMessage()));
        }

        ByteBuffer nativeBuffer = BufferUtils.createByteBuffer(buffer.capacity());
        nativeBuffer.put(buffer);

        return nativeBuffer;
    }
}
