package com.rayferric.comet.video.common;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.Monitor;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class Window {
    @Override
    public String toString() {
        return String.format("Window{handle=%s, vSync=%s, title=%s, pos=%s, size=%s, monitor=%s}",
                handle, vSync, title, pos, size, monitor);
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

    // May be called from any thread
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
            glfwSetWindowMonitor(handle, NULL, pos.getX(), pos.getY(), size.getX(), size.getY(), GLFW_DONT_CARE);
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

    // May be called from any thread
    public Vector2i getFramebufferSize() {
        // glfwGetFramebufferSize is not thread-safe
        framebufferSizeCacheLock.lock();
        Vector2i snapshot = new Vector2i(framebufferSizeCache);
        framebufferSizeCacheLock.unlock();
        return snapshot;
    }

    public void close() {
        glfwDestroyWindow(handle);
        handle = NULL;
    }

    protected void open(String title, int width, int height) {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        handle = glfwCreateWindow(width, height, this.title = title, NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create window instance.");

        glfwSetFramebufferSizeCallback(handle, this::framebufferSizeCallback);
        glfwSetKeyCallback(handle, this::keyCallback);
        glfwSetMouseButtonCallback(handle, this::mouseButtonCallback);
        glfwSetScrollCallback(handle, this::scrollCallback);

        final Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(getSize()).div(2));
        saveCurrentPlacement();

        framebufferSizeCallback(handle, width, height);

        setVSync(true);
    }

    private long handle;
    private boolean vSync;
    private String title;
    private Vector2i pos, size;
    private Monitor monitor = null;
    private final Vector2i framebufferSizeCache = new Vector2i();
    private final ReentrantLock framebufferSizeCacheLock = new ReentrantLock();

    private void saveCurrentPlacement() {
        pos = getPos();
        size = getSize();
    }

    private void framebufferSizeCallback(long window, int width, int height) {
        framebufferSizeCacheLock.lock();
        framebufferSizeCache.setX(width);
        framebufferSizeCache.setY(height);
        framebufferSizeCacheLock.unlock();
    }

    private void keyCallback(long window, int key, int scanCode, int action, int mods) {

    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
