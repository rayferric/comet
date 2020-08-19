package com.rayferric.comet.video.gl;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.common.Monitor;
import com.rayferric.comet.video.Window;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindow extends Window {
    public GLWindow(String title, Vector2i size) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);

        create(title, size);
    }

    public GLWindow(Window other) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);

        create(other.getTitle(), other.getSize());
        copyPropertiesFrom(other);
    }
}
