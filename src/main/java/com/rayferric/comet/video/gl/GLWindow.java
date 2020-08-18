package com.rayferric.comet.video.gl;

import com.rayferric.comet.video.common.Window;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindow extends Window {
    public GLWindow(String title, int width, int height) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        open(title, width, height);
    }
}
