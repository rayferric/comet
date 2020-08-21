package com.rayferric.comet.video;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.common.Monitor;
import com.rayferric.comet.video.common.WindowMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO Make this thread safe, finish Javadoc
public abstract class Window {
    @Override
    public String toString() {
        return String
                .format("Window{handle.get()=%s, title=%s, vSync=%s, pos=%s, size=%s, focus=%s, mode=%s, monitor=%s, visible=%s, framebufferSize=%s}",
                        handle.get(), getTitle(), hasVSync(), pos.get(), size.get(), hasFocus(), getMode(), getMonitor(), isVisible(),
                        getFramebufferSize());
    }

    /**
     * May be called from any thread.<br>
     */
    public static void makeCurrent(Window window) {
        if(window != null)
            glfwMakeContextCurrent(window.handle.get());
        else
            glfwMakeContextCurrent(NULL);
    }


    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public boolean isOpen() {
        // handle.get() is modified inside create(), which is only called by the constructor
        return handle.get() != NULL;
    }

    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle.get());
    }

    /**
     * May be called from any thread.<br>
     * Is not thread-safe.
     */
    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(handle.get(), shouldClose);
    }

    /**
     * Must only be called from the main thread.
     */
    public void destroy() {
        glfwDestroyWindow(handle.getAndSet(NULL));
    }

    /**
     * May be called from any thread.<br>
     * Is thread-safe.
     */
    public void swapBuffers() {
        glfwSwapBuffers(handle.get());
    }


    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle.get(), title);
        this.title.set(title);
    }

    public boolean hasVSync() {
        return vSync.get();
    }

    public void setVSync(boolean vSync) {
        if(this.vSync.get() == vSync)
            return;

        final long prevCurrent = glfwGetCurrentContext();
        glfwMakeContextCurrent(handle.get());
        glfwSwapInterval(vSync ? 1 : 0);
        glfwMakeContextCurrent(prevCurrent);
    }

    public Vector2i getPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(handle.get(), posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    public void setPos(Vector2i pos) {
        if(getMode() != WindowMode.WINDOWED)
            return;

        glfwSetWindowPos(handle.get(), pos.getX(), pos.getY());
    }

    public Vector2i getSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer sizeX = stack.mallocInt(1);
            final IntBuffer sizeY = stack.mallocInt(1);

            glfwGetWindowSize(handle.get(), sizeX, sizeY);

            return new Vector2i(sizeX.get(0), sizeY.get(0));
        }
    }

    public void setSize(Vector2i size) {
        if(getMode() != WindowMode.WINDOWED)
            return;

        glfwSetWindowSize(handle.get(), size.getX(), size.getY());
    }

    public boolean hasFocus() {
        return glfwGetWindowAttrib(handle.get(), GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle.get());
    }

    public WindowMode getMode() {
        if(monitor.get() != null)
            return WindowMode.FULLSCREEN;
        if(glfwGetWindowAttrib(handle.get(), GLFW_MAXIMIZED) == GLFW_TRUE)
            return WindowMode.MAXIMIZED;
        if(glfwGetWindowAttrib(handle.get(), GLFW_ICONIFIED) == GLFW_TRUE)
            return WindowMode.MINIMIZED;
        else
            return WindowMode.WINDOWED;
    }

    public void setMode(WindowMode mode) {
        if(mode != WindowMode.FULLSCREEN && getMonitor() != null)
            setMonitor(null);

        switch(mode) {
            case WINDOWED -> glfwRestoreWindow(handle.get());
            case MINIMIZED -> glfwIconifyWindow(handle.get());
            case MAXIMIZED -> glfwMaximizeWindow(handle.get());
            case FULLSCREEN -> setMonitor(Monitor.getPrimary());
        }
    }

    public Monitor getMonitor() {
        return monitor.get();
    }

    public void setMonitor(Monitor monitor) {
        if(this.monitor.get() == monitor) return;
        this.monitor.set(monitor);

        if(monitor != null) {
            final Vector2i resolution = monitor.getResolution();
            glfwSetWindowMonitor(handle.get(), monitor.getHandle(), 0, 0, resolution.getX(), resolution.getY(),
                    GLFW_DONT_CARE);
        } else {
            Vector2i pos = this.pos.get();
            Vector2i size = this.size.get();
            glfwSetWindowMonitor(handle.get(), NULL, pos.getX(), pos.getY(), size.getX(), size.getY(), GLFW_DONT_CARE);
        }
    }

    public boolean isVisible() {
        return glfwGetWindowAttrib(handle.get(), GLFW_VISIBLE) == GLFW_TRUE;
    }

    public void setVisible(boolean visible) {
        if(visible)
            glfwShowWindow(handle.get());
        else
            glfwHideWindow(handle.get());
    }

    // May be called from any thread
    public Vector2i getFramebufferSize() {
        // glfwGetFramebufferSize is not thread-safe, so here we are
        synchronized(framebufferSizeCache) {
            return new Vector2i(framebufferSizeCache);
        }
    }

    protected abstract String getCreationFailMessage();

    protected abstract void requireExtensions();

    protected void create(String title, Vector2i size) {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        handle.set(glfwCreateWindow(size.getX(), size.getY(), title, NULL, NULL));
        if(handle.get() == NULL)
            throw new RuntimeException(getCreationFailMessage());

        makeCurrent(this);
        requireExtensions();
        makeCurrent(null);

        glfwSetFramebufferSizeCallback(handle.get(), this::framebufferSizeCallback);
        glfwSetKeyCallback(handle.get(), this::keyCallback);
        glfwSetMouseButtonCallback(handle.get(), this::mouseButtonCallback);
        glfwSetScrollCallback(handle.get(), this::scrollCallback);
        glfwSetWindowPosCallback(handle.get(), this::windowPosCallback);
        glfwSetWindowSizeCallback(handle.get(), this::windowSizeCallback);

        // We must make sure all these values are initialized:

        this.title.set(title);
        setVSync(true);

        Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(size).div(2));
        else
            this.pos.set(getPos());
        this.size.set(size);

        Vector2i fbSize = getFramebufferSize();
        framebufferSizeCallback(handle.get(), fbSize.getX(), fbSize.getY());
    }

    protected void copyPropertiesFrom(Window other) {
        setVSync(other.hasVSync());

        setPos(other.pos.get());
        setSize(other.size.get());

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

    private final AtomicLong handle = new AtomicLong();
    private final AtomicReference<String> title = new AtomicReference<>();
    private final AtomicBoolean vSync = new AtomicBoolean();
    private final AtomicReference<Vector2i> pos = new AtomicReference<>(), size = new AtomicReference<>();
    private final AtomicReference<Monitor> monitor = new AtomicReference<>(null);
    private final Vector2i framebufferSizeCache = new Vector2i();

    private void windowPosCallback(long window, int x, int y) {
        if(getMode() == WindowMode.WINDOWED)
            pos.set(new Vector2i(x, y));
    }

    private void windowSizeCallback(long window, int width, int height) {
        if(getMode() == WindowMode.WINDOWED)
            size.set(new Vector2i(width, height));
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
            Engine.getInstance().getVideoServer().setApi(VideoAPI.OPENGL);

        if(key == GLFW_KEY_F)
            if(Engine.getInstance().getVideoServer().getWindow().getMode() == WindowMode.FULLSCREEN)
                Engine.getInstance().getVideoServer().getWindow().setMode(WindowMode.WINDOWED);
            else
                Engine.getInstance().getVideoServer().getWindow().setMode(WindowMode.FULLSCREEN);
    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
