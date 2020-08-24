package com.rayferric.comet.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.Monitor;
import com.rayferric.comet.video.util.texture.TextureFilter;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicLong;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO Finish Javadoc, add cursor pos query caching similar to getFramebufferSize
public abstract class Window {
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
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle.get(), title);
        this.title = title;
    }

    public Vector2i getPos() {
        return windowedPos;
    }

    public void setPos(Vector2i pos) {
        if(fullscreen) return;
        glfwSetWindowPos(handle.get(), pos.getX(), pos.getY());
    }

    public Vector2i getSize() {
        return windowedSize;
    }

    public void setSize(Vector2i size) {
        if(fullscreen) return;
        glfwSetWindowSize(handle.get(), size.getX(), size.getY());
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        if(fullscreen) return;
        this.maximized = maximized;
        applyWindowMode();
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void setMinimized(boolean minimized) {
        if(fullscreen) return;
        this.minimized = minimized;
        applyWindowMode();
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    // Makes the window visible, sets both maximized and minimized to false, and goes fullscreen on a specified monitor.
    public void setFullscreen(boolean fullscreen) {
        if(this.fullscreen == fullscreen || monitor == null) return;
        this.fullscreen = fullscreen;

        if(fullscreen) {
            setVisible(true);
            maximized = minimized = false;
            applyWindowMode();

            final Vector2i res = monitor.getResolution();
            glfwSetWindowMonitor(handle.get(), monitor.getHandle(), 0, 0, res.getX(), res.getY(), GLFW_DONT_CARE);
        } else {
            glfwSetWindowMonitor(handle.get(), NULL, windowedPos.getX(), windowedPos.getY(), windowedSize.getX(),
                    windowedSize.getY(), GLFW_DONT_CARE);
            applyWindowMode();
        }
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
        if(fullscreen) {
            setFullscreen(false);
            setFullscreen(true);
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

    public boolean hasFocus() {
        return glfwGetWindowAttrib(handle.get(), GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle.get());
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

        long prevHandle = glfwGetCurrentContext();
        glfwMakeContextCurrent(handle.get());
        requireExtensions();
        glfwMakeContextCurrent(prevHandle);

        glfwSetWindowPosCallback(handle.get(), this::windowPosCallback);
        glfwSetWindowSizeCallback(handle.get(), this::windowSizeCallback);
        glfwSetWindowIconifyCallback(handle.get(), this::windowIconifyCallback);
        glfwSetWindowMaximizeCallback(handle.get(), this::windowMaximizeCallback);
        glfwSetFramebufferSizeCallback(handle.get(), this::framebufferSizeCallback);
        glfwSetKeyCallback(handle.get(), this::keyCallback);
        glfwSetMouseButtonCallback(handle.get(), this::mouseButtonCallback);
        glfwSetScrollCallback(handle.get(), this::scrollCallback);

        // Make sure these values are initialized:
        this.title = title;
        this.windowedPos = getCurrentPos();
        this.windowedSize = getCurrentSize();

        // Center the window if possible:
        Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(size).div(2));

        // Update the framebuffer size cache:
        Vector2i fbSize = getFramebufferSize();
        framebufferSizeCallback(handle.get(), fbSize.getX(), fbSize.getY());
    }

    protected void copyPropertiesFrom(Window other) {
        setPos(other.getPos());

        setMonitor(Monitor.getPrimary());
        if(other.isFullscreen()) {
            // Overwrite the current monitor owner
            other.setFullscreen(false);
            setFullscreen(true);
        }

        setMaximized(other.isMaximized());
        setMinimized(other.isMinimized());

        setVisible(other.isVisible());
        if(other.hasFocus()) focus();
    }

    private final AtomicLong handle = new AtomicLong();
    private String title;
    private Vector2i windowedPos;
    private Vector2i windowedSize;
    private boolean maximized = false;
    private boolean minimized = false;
    private boolean fullscreen = false;
    private Monitor monitor = Monitor.getPrimary();
    private final Vector2i framebufferSizeCache = new Vector2i();

    private Vector2i getCurrentPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(handle.get(), posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    private Vector2i getCurrentSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer sizeX = stack.mallocInt(1);
            final IntBuffer sizeY = stack.mallocInt(1);

            glfwGetWindowSize(handle.get(), sizeX, sizeY);

            return new Vector2i(sizeX.get(0), sizeY.get(0));
        }
    }

    private boolean isCurrentlyMaximized() {
        return glfwGetWindowAttrib(handle.get(), GLFW_MAXIMIZED) == GLFW_TRUE;
    }

    private boolean isCurrentlyMinimized() {
        return glfwGetWindowAttrib(handle.get(), GLFW_ICONIFIED) == GLFW_TRUE;
    }

    private void applyWindowMode() {
        // Minimization overrides maximization, they both override windowed mode:
        if(maximized) glfwMaximizeWindow(handle.get());
        if(minimized) glfwIconifyWindow(handle.get());
        if(!maximized && !minimized) glfwRestoreWindow(handle.get());
    }

    private void windowPosCallback(long window, int x, int y) {
        // We can't rely on the tracking variables as callbacks may fire in the wrong order
        final boolean maximized = isCurrentlyMaximized();
        final boolean minimized = isCurrentlyMinimized();
        if(fullscreen || maximized || minimized) return;
        windowedPos = new Vector2i(x, y);
    }

    private void windowSizeCallback(long window, int width, int height) {
        // We can't rely on the tracking variables as callbacks may fire in the wrong order
        final boolean maximized = isCurrentlyMaximized();
        final boolean minimized = isCurrentlyMinimized();
        if(fullscreen || maximized || minimized) return;
        windowedSize = new Vector2i(width, height);
    }

    private void windowIconifyCallback(long window, boolean iconified) {
        minimized = iconified;
    }

    private void windowMaximizeCallback(long window, boolean maximized) {
        this.maximized = maximized;
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
            Engine.getInstance().getVideoServer().getWindow().setFullscreen(
                    !Engine.getInstance().getVideoServer().getWindow().isFullscreen());

        if(key == GLFW_KEY_T)
            if(Engine.getInstance().getVideoServer().getTextureFilter() == TextureFilter.NEAREST)
                Engine.getInstance().getVideoServer().setTextureFilter(TextureFilter.TRILINEAR);
            else
                Engine.getInstance().getVideoServer().setTextureFilter(TextureFilter.NEAREST);
    }

    private void mouseButtonCallback(long window, int button, int action, int mods) {

    }

    private void scrollCallback(long window, double offsetX, double offsetY) {

    }
}
