package com.rayferric.comet.video.display;

import com.rayferric.comet.math.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public Window(String title, int width, int height) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);

        handle = glfwCreateWindow(width, height, this.title = title, NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create window instance.");

        glfwSetKeyCallback(handle, this::keyCallback);
        glfwSetMouseButtonCallback(handle, this::mouseButtonCallback);
        glfwSetScrollCallback(handle, this::scrollCallback);

        final Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(getSize()).div(2));
        saveCurrentPlacement();

        setVSync(true);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Window other = (Window)o;
        return handle == other.handle;
    }

    @Override
    public String toString() {
        return String.format("Window{handle=%s, vSync=%s, title=%s, posX=%s, posY=%s, sizeX=%s, sizeY=%s, monitor=%s}",
                handle, vSync, title, posX, posY, sizeX, sizeY, monitor);
    }

    public static void pollEvents() {
        glfwPollEvents();
    }

    public static void makeCurrent(Window window) {
        if(window != null)
            glfwMakeContextCurrent(window.handle);
        else
            glfwMakeContextCurrent(NULL);
    }

    public boolean isOpen() {
        return handle != NULL;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(handle, shouldClose);
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public boolean hasVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        if(this.vSync == vSync)
            return;

        final long prevCurrent = glfwGetCurrentContext();
        glfwMakeContextCurrent(handle);
        glfwSwapInterval((this.vSync = vSync) ? 1 : 0);
        glfwMakeContextCurrent(prevCurrent);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle, this.title = title);
    }

    public Vector2i getPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(handle, posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    public void setPos(Vector2i pos) {
        glfwSetWindowPos(handle, pos.getX(), pos.getY());
    }

    public Vector2i getSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer sizeX = stack.mallocInt(1);
            final IntBuffer sizeY = stack.mallocInt(1);

            glfwGetWindowSize(handle, sizeX, sizeY);

            return new Vector2i(sizeX.get(0), sizeY.get(0));
        }
    }

    public void setSize(Vector2i size) {
        glfwSetWindowPos(handle, size.getX(), size.getY());
    }

    public boolean hasFocus() {
        return glfwGetWindowAttrib(handle, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle);
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
            glfwSetWindowMonitor(handle, monitor.getHandle(), 0, 0, resolution.getX(), resolution.getY(),
                    GLFW_DONT_CARE);
        } else
            glfwSetWindowMonitor(handle, NULL, posX.get(0), posY.get(0), sizeX.get(0), sizeY.get(0), GLFW_DONT_CARE);
    }

    public boolean isVisible() {
        return glfwGetWindowAttrib(handle, GLFW_VISIBLE) == GLFW_TRUE;
    }

    public void setVisible(boolean visible) {
        if(visible)
            glfwShowWindow(handle);
        else
            glfwHideWindow(handle);
    }

    public void close() {
        glfwDestroyWindow(handle);
        handle = NULL;
    }

    private long handle;
    private boolean vSync;
    private String title;
    private final IntBuffer posX = BufferUtils.createIntBuffer(1);
    private final IntBuffer posY = BufferUtils.createIntBuffer(1);
    private final IntBuffer sizeX = BufferUtils.createIntBuffer(1);
    private final IntBuffer sizeY = BufferUtils.createIntBuffer(1);
    private Monitor monitor = null;

    private void saveCurrentPlacement() {
        glfwGetWindowPos(handle, posX, posY);
        glfwGetWindowSize(handle, sizeX, sizeY);
    }

    private void keyCallback(long window, int key, int scanCode, int action, int mods) {

    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
