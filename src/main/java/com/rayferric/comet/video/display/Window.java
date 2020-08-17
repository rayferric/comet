package com.rayferric.comet.video.display;

import com.rayferric.comet.math.Vector2i;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public Window(String title, int width, int height) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);

        window = glfwCreateWindow(width, height, this.title = title, NULL, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create window instance.");

        glfwSetKeyCallback(window, this::keyCallback);
        glfwSetMouseButtonCallback(window, this::mouseButtonCallback);
        glfwSetScrollCallback(window, this::scrollCallback);

        final Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(getSize()).div(2));
        saveCurrentPlacement();

        makeCurrent();
        setVSync(true);
        GL.createCapabilities();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Window other = (Window)obj;
        return window == other.window &&
                vSync == other.vSync &&
                Objects.equals(title, other.title) &&
                Objects.equals(posX, other.posX) &&
                Objects.equals(posY, other.posY) &&
                Objects.equals(sizeX, other.sizeX) &&
                Objects.equals(sizeY, other.sizeY) &&
                Objects.equals(monitor, other.monitor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(window, vSync, title, posX, posY, sizeX, sizeY, monitor);
    }

    @Override
    public String toString() {
        return String.format("Window{window=%s, vSync=%s, title=%s, posX=%s, posY=%s, sizeX=%s, sizeY=%s, monitor=%s}",
                window, vSync, title, posX, posY, sizeX, sizeY, monitor);
    }

    public static void pollEvents() {
        glfwPollEvents();
    }

    public boolean isOpen() {
        return window != NULL;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void makeCurrent() {
        makeCurrent(window);
    }

    public boolean hasVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        if(this.vSync == vSync)
            return;

        final long prevCurrent = Window.current;
        makeCurrent();

        glfwSwapInterval((this.vSync = vSync) ? 1 : 0);

        makeCurrent(prevCurrent);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, this.title = title);
    }

    public Vector2i getPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(window, posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    public void setPos(Vector2i pos) {
        glfwSetWindowPos(window, pos.getX(), pos.getY());
    }

    public Vector2i getSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer sizeX = stack.mallocInt(1);
            final IntBuffer sizeY = stack.mallocInt(1);

            glfwGetWindowSize(window, sizeX, sizeY);

            return new Vector2i(sizeX.get(0), sizeY.get(0));
        }
    }

    public void setSize(Vector2i size) {
        glfwSetWindowPos(window, size.getX(), size.getY());
    }

    public boolean hasFocus() {
        return glfwGetWindowAttrib(window, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(window);
    }

    public boolean isFullscreen() {
        return monitor != null;
    }

    public void setFullscreen(Monitor monitor) {
        if(this.monitor == monitor)
            return;
        this.monitor = monitor;

        if(monitor != null) {
            saveCurrentPlacement();
            final Vector2i resolution = monitor.getResolution();
            glfwSetWindowMonitor(window, monitor.getHandle(), 0, 0, resolution.getX(), resolution.getY(),
                    GLFW_DONT_CARE);
        } else
            glfwSetWindowMonitor(window, NULL, posX.get(0), posY.get(0), sizeX.get(0), sizeY.get(0), GLFW_DONT_CARE);
    }

    public boolean isVisible() {
        return glfwGetWindowAttrib(window, GLFW_VISIBLE) == GLFW_TRUE;
    }

    public void setVisible(boolean visible) {
        if(visible)
            glfwShowWindow(window);
        else
            glfwHideWindow(window);
    }

    public void close() {
        glfwDestroyWindow(window);
        window = NULL;
    }

    private static long current = NULL;

    private long window;
    private boolean vSync;
    private String title;
    private final IntBuffer posX = IntBuffer.allocate(1);
    private final IntBuffer posY = IntBuffer.allocate(1);
    private final IntBuffer sizeX = IntBuffer.allocate(1);
    private final IntBuffer sizeY = IntBuffer.allocate(1);
    private Monitor monitor = null;

    private static void makeCurrent(final long window) {
        if(current != window) {
            glfwMakeContextCurrent(current = window);
        }
    }

    private void saveCurrentPlacement() {
        glfwGetWindowPos(window, posX, posY);
        glfwGetWindowSize(window, sizeX, sizeY);
    }

    private void keyCallback(long window, int key, int scanCode, int action, int mods) {

    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
