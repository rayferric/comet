package com.rayferric.comet.video.display;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

public class GLFW {
    public static void init() {
        if(initialized)return;
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
