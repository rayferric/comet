package com.rayferric.comet.video.display;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class GLFW {
    public static void init() {
        if(initialized) return;
        initialized = true;

        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Failed to initialize GLFW.");
    }

    public static void terminate() {
        glfwTerminate();
        initialized = false;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static boolean initialized = false;
}
