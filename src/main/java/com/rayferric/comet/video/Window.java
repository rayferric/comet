package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.common.Monitor;
import com.rayferric.comet.video.common.VideoAPI;
import com.rayferric.comet.video.common.WindowMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO Make this thread safe, finish Javadoc
public abstract class Window {
    @Override
    public String toString() {
        return String
                .format("Window{handle=%s, title=%s, vSync=%s, pos=%s, size=%s, focus=%s, mode=%s, monitor=%s, visible=%s, framebufferSize=%s}",
                        handle, title, vSync, pos, size, hasFocus(), getMode(), monitor, isVisible(),
                        getFramebufferSize());
    }


    /**
     * Must only be called from the main thread.
     */
    public static void pollEvents() {
        glfwPollEvents();
    }

    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public static void makeCurrent(Window window) {
        if(window != null)
            glfwMakeContextCurrent(window.handle);
        else
            glfwMakeContextCurrent(NULL);
    }


    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public boolean isOpen() {
        // handle is modified inside create(), which is only called by the constructor
        return handle != NULL;
    }

    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    /**
     * May be called from any thread.<br>
     * Is not thread-safe.
     */
    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(handle, shouldClose);
    }

    /**
     * Must only be called from the main thread.
     */
    public void destroy() {
        glfwDestroyWindow(handle);
        handle = NULL;
    }

    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle, this.title = title);
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

    public Vector2i getPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(handle, posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    public void setPos(Vector2i pos) {
        if(getMode() != WindowMode.WINDOWED)
            return;

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
        if(getMode() != WindowMode.WINDOWED)
            return;

        glfwSetWindowSize(handle, size.getX(), size.getY());
    }

    public boolean hasFocus() {
        return glfwGetWindowAttrib(handle, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle);
    }

    public WindowMode getMode() {
        if(monitor != null)
            return WindowMode.FULLSCREEN;
        if(glfwGetWindowAttrib(handle, GLFW_MAXIMIZED) == GLFW_TRUE)
            return WindowMode.MAXIMIZED;
        if(glfwGetWindowAttrib(handle, GLFW_ICONIFIED) == GLFW_TRUE)
            return WindowMode.MINIMIZED;
        else
            return WindowMode.WINDOWED;
    }

    public void setMode(WindowMode mode) {
        if(mode != WindowMode.FULLSCREEN && getMonitor() != null)
            setMonitor(null);

        switch(mode) {
            default:
            case WINDOWED:
                glfwRestoreWindow(handle);
                break;
            case MINIMIZED:
                glfwIconifyWindow(handle);
                break;
            case MAXIMIZED:
                glfwMaximizeWindow(handle);
                break;
            case FULLSCREEN:
                setMonitor(Monitor.getPrimary());
                break;
        }
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        if(this.monitor == monitor)
            return;
        this.monitor = monitor;

        if(monitor != null) {
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
        // glfwGetFramebufferSize is not thread-safe, so here we are
        synchronized(framebufferSizeCache) {
            return new Vector2i(framebufferSizeCache);
        }
    }

    protected void create(String title, Vector2i size) {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        handle = glfwCreateWindow(size.getX(), size.getY(), this.title = title, NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create window instance.");

        glfwSetFramebufferSizeCallback(handle, this::framebufferSizeCallback);
        glfwSetKeyCallback(handle, this::keyCallback);
        glfwSetMouseButtonCallback(handle, this::mouseButtonCallback);
        glfwSetScrollCallback(handle, this::scrollCallback);
        glfwSetWindowPosCallback(handle, this::windowPosCallback);
        glfwSetWindowSizeCallback(handle, this::windowSizeCallback);

        setVSync(true);

        Vector2i fbSize = getFramebufferSize();
        framebufferSizeCallback(handle, fbSize.getX(), fbSize.getY());
    }

    protected void copyPropertiesFrom(Window other) {
        setVSync(other.hasVSync());

        setPos(other.pos);
        setSize(other.size);

        if(other.hasFocus()) focus();

        Monitor monitor = other.getMonitor();
        if(monitor != null) {
            // Set to visible, otherwise stuff goes crazy
            setVisible(true);
            setMonitor(monitor);
        } else
            setMode(other.getMode());

        setVisible(other.isVisible());
    }


    private long handle;
    private String title;
    private boolean vSync;
    private Vector2i pos, size;
    private Monitor monitor = null;
    private final Vector2i framebufferSizeCache = new Vector2i();

    private void windowPosCallback(long window, int x, int y) {
        if(getMode() == WindowMode.WINDOWED)
            pos = new Vector2i(x, y);
    }

    private void windowSizeCallback(long window, int width, int height) {
        if(getMode() == WindowMode.WINDOWED)
            size = new Vector2i(width, height);
    }

    private void framebufferSizeCallback(long window, int width, int height) {
        synchronized(framebufferSizeCache) {
            framebufferSizeCache.setX(width);
            framebufferSizeCache.setY(height);
        }
    }

    private void keyCallback(long window, int key, int scanCode, int action, int mods) {
        if(action != GLFW_RELEASE) return;

        if(key == GLFW_KEY_R)
            Engine.getInstance().changeVideoApi(VideoAPI.OPENGL);
    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
