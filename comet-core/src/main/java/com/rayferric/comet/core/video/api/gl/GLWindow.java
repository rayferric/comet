package com.rayferric.comet.core.video.api.gl;

import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.video.Window;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindow extends Window {
    public GLWindow(String title, Vector2i size) {
        setUpGl();
        create(title, size);
    }

    public GLWindow(Window other) {
        setUpGl();
        create(other.getTitle(), other.getSize());
        copyPropertiesFrom(other);
    }

    @Override
    protected String getCreationFailMessage() {
        return "Failed to create window instance.\nOpenGL 4.3 is required.";
    }

    @Override
    protected void requireExtensions() {
        requireExtension("GL_ARB_gl_spirv");
        requireExtension("GL_EXT_texture_filter_anisotropic");
    }

    private void setUpGl() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    @SuppressWarnings("SameParameterValue")
    private void requireExtension(String name) {
        if(!glfwExtensionSupported(name))
            throw new RuntimeException(
                    String.format("Failed to create window instance.\nRequired OpenGL extension %s is missing.", name));
    }
}
