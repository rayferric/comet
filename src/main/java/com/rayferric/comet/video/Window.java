package com.rayferric.comet.video;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.*;
import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.input.event.KeyInputEvent;
import com.rayferric.comet.input.event.TextInputEvent;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.util.Monitor;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO Finish Javadoc, add cursor pos query caching similar to getFramebufferSize
public abstract class Window {
    /**
     * Must only be called from the main thread, preferably before processing system events.
     */
    public void process() {
        int cursorMode = glfwGetInputMode(handle, GLFW_CURSOR);
        if(Engine.getInstance().getInputManager().isMouseHidden()) {
            if(cursorMode != GLFW_CURSOR_HIDDEN)
                glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if(cursorMode != GLFW_CURSOR_NORMAL)
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

        Vector2i newPos = getGlfwCursorPos();
        Vector2i deltaPos = newPos.sub(lastCursorPos);
        lastCursorPos = newPos;

        InputManager inputManager = Engine.getInstance().getInputManager();
        if(inputManager.isMouseCentered()) {
            lastCursorPos = getGlfwSize().div(2);
            glfwSetCursorPos(handle, lastCursorPos.getX(), lastCursorPos.getY());
        } else lastCursorPos = newPos;

        inputManager.setAxisValue(InputAxis.MOUSE_X, deltaPos.getX());
        inputManager.setAxisValue(InputAxis.MOUSE_Y, deltaPos.getY());
    }

    /**
     * Must only be called from the main thread.
     */
    public void destroy() {
        glfwDestroyWindow(handle);
    }

    /**
     * May be called from any thread.<br>
     */
    public static void makeCurrent(Window window) {
        if(window != null)
            glfwMakeContextCurrent(window.handle);
        else
            glfwMakeContextCurrent(NULL);
    }

    /**
     * May be called from any thread.
     */
    public boolean isOpen() {
        // handle is modified inside create(), which is only called by the constructor
        return handle != NULL;
    }

    /**
     * May be called from any thread.
     */
    public boolean shouldClose() {
        synchronized(shouldCloseLock) {
            return glfwWindowShouldClose(handle);
        }
    }

    /**
     * May be called from any thread.
     */
    public void setShouldClose(boolean shouldClose) {
        synchronized(shouldCloseLock) {
            glfwSetWindowShouldClose(handle, shouldClose);
        }
    }

    /**
     * May be called from any thread.<br>
     */
    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    public Vector2i getPos() {
        return windowedPos;
    }

    public void setPos(Vector2i pos) {
        if(fullscreen) return;
        glfwSetWindowPos(handle, pos.getX(), pos.getY());
    }

    public Vector2i getSize() {
        return windowedSize;
    }

    public void setSize(Vector2i size) {
        if(fullscreen) return;
        glfwSetWindowSize(handle, size.getX(), size.getY());
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

            Vector2i size = monitor.getResolution();
            glfwSetWindowMonitor(handle, monitor.getHandle(), 0, 0, size.getX(), size.getY(), GLFW_DONT_CARE);
        } else {
            glfwSetWindowMonitor(handle, NULL, windowedPos.getX(), windowedPos.getY(), windowedSize.getX(),
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
        return glfwGetWindowAttrib(handle, GLFW_VISIBLE) == GLFW_TRUE;
    }

    public void setVisible(boolean visible) {
        if(visible)
            glfwShowWindow(handle);
        else
            glfwHideWindow(handle);
    }

    public boolean hasFocus() {
        return glfwGetWindowAttrib(handle, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle);
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

        handle = glfwCreateWindow(size.getX(), size.getY(), title, NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException(getCreationFailMessage());

        long prevHandle = glfwGetCurrentContext();
        glfwMakeContextCurrent(handle);
        requireExtensions();
        glfwMakeContextCurrent(prevHandle);

        glfwSetWindowPosCallback(handle, this::windowPosCallback);
        glfwSetWindowSizeCallback(handle, this::windowSizeCallback);
        glfwSetWindowIconifyCallback(handle, this::windowIconifyCallback);
        glfwSetWindowMaximizeCallback(handle, this::windowMaximizeCallback);
        glfwSetFramebufferSizeCallback(handle, this::framebufferSizeCallback);
        glfwSetKeyCallback(handle, Window::keyCallback);
        glfwSetCharCallback(handle, Window::charCallback);
        glfwSetMouseButtonCallback(handle, Window::mouseButtonCallback);
        glfwSetScrollCallback(handle, Window::scrollCallback);

        // Make sure these values are initialized:
        this.title = title;
        this.windowedPos = getGlfwPos();
        this.windowedSize = getGlfwSize();

        // Center the window if possible:
        Monitor monitor = Monitor.getPrimary();
        if(monitor != null)
            setPos(monitor.getResolution().sub(size).div(2));

        // Update the framebuffer size cache:
        Vector2i fbSize = getFramebufferSize();
        framebufferSizeCallback(handle, fbSize.getX(), fbSize.getY());
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

    // <editor-fold desc="GLFW Input Code Mappings">

    private static final Map<Integer, InputKey> GLFW_MOUSE_BUTTON_MAP = Map.<Integer, InputKey>ofEntries(
            Map.entry(GLFW_MOUSE_BUTTON_LEFT, InputKey.MOUSE_LEFT),
            Map.entry(GLFW_MOUSE_BUTTON_RIGHT, InputKey.MOUSE_RIGHT),
            Map.entry(GLFW_MOUSE_BUTTON_MIDDLE, InputKey.MOUSE_MIDDLE),
            // This is untested, these mappings may be invalid.
            Map.entry(GLFW_MOUSE_BUTTON_4, InputKey.MOUSE_FORWARD),
            Map.entry(GLFW_MOUSE_BUTTON_5, InputKey.MOUSE_BACKWARD)
    );

    private static final Map<Integer, InputKey> GLFW_KEY_MAP = Map.<Integer, InputKey>ofEntries(
            Map.entry(GLFW_KEY_BACKSPACE, InputKey.KEYBOARD_BACKSPACE),
            Map.entry(GLFW_KEY_TAB, InputKey.KEYBOARD_TAB),
            Map.entry(GLFW_KEY_ENTER, InputKey.KEYBOARD_RETURN),
            Map.entry(GLFW_KEY_CAPS_LOCK, InputKey.KEYBOARD_CAPSLOCK),
            Map.entry(GLFW_KEY_ESCAPE, InputKey.KEYBOARD_ESCAPE),
            Map.entry(GLFW_KEY_SPACE, InputKey.KEYBOARD_SPACE),
            Map.entry(GLFW_KEY_PAGE_UP, InputKey.KEYBOARD_PAGEUP),
            Map.entry(GLFW_KEY_PAGE_DOWN, InputKey.KEYBOARD_PAGEDOWN),
            Map.entry(GLFW_KEY_END, InputKey.KEYBOARD_END),
            Map.entry(GLFW_KEY_HOME, InputKey.KEYBOARD_HOME),

            Map.entry(GLFW_KEY_UP, InputKey.KEYBOARD_UP),
            Map.entry(GLFW_KEY_DOWN, InputKey.KEYBOARD_DOWN),
            Map.entry(GLFW_KEY_LEFT, InputKey.KEYBOARD_LEFT),
            Map.entry(GLFW_KEY_RIGHT, InputKey.KEYBOARD_RIGHT),

            Map.entry(GLFW_KEY_PRINT_SCREEN, InputKey.KEYBOARD_PRINTSCREEN),
            Map.entry(GLFW_KEY_INSERT, InputKey.KEYBOARD_INSERT),
            Map.entry(GLFW_KEY_DELETE, InputKey.KEYBOARD_DELETE),

            Map.entry(GLFW_KEY_0, InputKey.KEYBOARD_0),
            Map.entry(GLFW_KEY_1, InputKey.KEYBOARD_1),
            Map.entry(GLFW_KEY_2, InputKey.KEYBOARD_2),
            Map.entry(GLFW_KEY_3, InputKey.KEYBOARD_3),
            Map.entry(GLFW_KEY_4, InputKey.KEYBOARD_4),
            Map.entry(GLFW_KEY_5, InputKey.KEYBOARD_5),
            Map.entry(GLFW_KEY_6, InputKey.KEYBOARD_6),
            Map.entry(GLFW_KEY_7, InputKey.KEYBOARD_7),
            Map.entry(GLFW_KEY_8, InputKey.KEYBOARD_8),
            Map.entry(GLFW_KEY_9, InputKey.KEYBOARD_9),
            Map.entry(GLFW_KEY_A, InputKey.KEYBOARD_A),
            Map.entry(GLFW_KEY_B, InputKey.KEYBOARD_B),
            Map.entry(GLFW_KEY_C, InputKey.KEYBOARD_C),
            Map.entry(GLFW_KEY_D, InputKey.KEYBOARD_D),
            Map.entry(GLFW_KEY_E, InputKey.KEYBOARD_E),
            Map.entry(GLFW_KEY_F, InputKey.KEYBOARD_F),
            Map.entry(GLFW_KEY_G, InputKey.KEYBOARD_G),
            Map.entry(GLFW_KEY_H, InputKey.KEYBOARD_H),
            Map.entry(GLFW_KEY_I, InputKey.KEYBOARD_I),
            Map.entry(GLFW_KEY_J, InputKey.KEYBOARD_J),
            Map.entry(GLFW_KEY_K, InputKey.KEYBOARD_K),
            Map.entry(GLFW_KEY_L, InputKey.KEYBOARD_L),
            Map.entry(GLFW_KEY_M, InputKey.KEYBOARD_M),
            Map.entry(GLFW_KEY_N, InputKey.KEYBOARD_N),
            Map.entry(GLFW_KEY_O, InputKey.KEYBOARD_O),
            Map.entry(GLFW_KEY_P, InputKey.KEYBOARD_P),
            Map.entry(GLFW_KEY_Q, InputKey.KEYBOARD_Q),
            Map.entry(GLFW_KEY_R, InputKey.KEYBOARD_R),
            Map.entry(GLFW_KEY_S, InputKey.KEYBOARD_S),
            Map.entry(GLFW_KEY_T, InputKey.KEYBOARD_T),
            Map.entry(GLFW_KEY_U, InputKey.KEYBOARD_U),
            Map.entry(GLFW_KEY_V, InputKey.KEYBOARD_V),
            Map.entry(GLFW_KEY_W, InputKey.KEYBOARD_W),
            Map.entry(GLFW_KEY_X, InputKey.KEYBOARD_X),
            Map.entry(GLFW_KEY_Y, InputKey.KEYBOARD_Y),
            Map.entry(GLFW_KEY_Z, InputKey.KEYBOARD_Z),

            Map.entry(GLFW_KEY_KP_0, InputKey.KEYBOARD_NUMPAD_0),
            Map.entry(GLFW_KEY_KP_1, InputKey.KEYBOARD_NUMPAD_1),
            Map.entry(GLFW_KEY_KP_2, InputKey.KEYBOARD_NUMPAD_2),
            Map.entry(GLFW_KEY_KP_3, InputKey.KEYBOARD_NUMPAD_3),
            Map.entry(GLFW_KEY_KP_4, InputKey.KEYBOARD_NUMPAD_4),
            Map.entry(GLFW_KEY_KP_5, InputKey.KEYBOARD_NUMPAD_5),
            Map.entry(GLFW_KEY_KP_6, InputKey.KEYBOARD_NUMPAD_6),
            Map.entry(GLFW_KEY_KP_7, InputKey.KEYBOARD_NUMPAD_7),
            Map.entry(GLFW_KEY_KP_8, InputKey.KEYBOARD_NUMPAD_8),
            Map.entry(GLFW_KEY_KP_9, InputKey.KEYBOARD_NUMPAD_9),

            Map.entry(GLFW_KEY_KP_MULTIPLY, InputKey.KEYBOARD_MULTIPLY),
            Map.entry(GLFW_KEY_KP_ADD, InputKey.KEYBOARD_ADD),
            Map.entry(GLFW_KEY_KP_SUBTRACT, InputKey.KEYBOARD_SUBTRACT),
            Map.entry(GLFW_KEY_KP_DECIMAL, InputKey.KEYBOARD_DECIMAL),
            Map.entry(GLFW_KEY_KP_DIVIDE, InputKey.KEYBOARD_DIVIDE),
            Map.entry(GLFW_KEY_KP_ENTER, InputKey.KEYBOARD_ENTER),

            Map.entry(GLFW_KEY_F1, InputKey.KEYBOARD_F1),
            Map.entry(GLFW_KEY_F2, InputKey.KEYBOARD_F2),
            Map.entry(GLFW_KEY_F3, InputKey.KEYBOARD_F3),
            Map.entry(GLFW_KEY_F4, InputKey.KEYBOARD_F4),
            Map.entry(GLFW_KEY_F5, InputKey.KEYBOARD_F5),
            Map.entry(GLFW_KEY_F6, InputKey.KEYBOARD_F6),
            Map.entry(GLFW_KEY_F7, InputKey.KEYBOARD_F7),
            Map.entry(GLFW_KEY_F8, InputKey.KEYBOARD_F8),
            Map.entry(GLFW_KEY_F9, InputKey.KEYBOARD_F9),
            Map.entry(GLFW_KEY_F10, InputKey.KEYBOARD_F10),
            Map.entry(GLFW_KEY_F11, InputKey.KEYBOARD_F11),
            Map.entry(GLFW_KEY_F12, InputKey.KEYBOARD_F12),
            Map.entry(GLFW_KEY_F13, InputKey.KEYBOARD_F13),
            Map.entry(GLFW_KEY_F14, InputKey.KEYBOARD_F14),
            Map.entry(GLFW_KEY_F15, InputKey.KEYBOARD_F15),
            Map.entry(GLFW_KEY_F16, InputKey.KEYBOARD_F16),
            Map.entry(GLFW_KEY_F17, InputKey.KEYBOARD_F17),
            Map.entry(GLFW_KEY_F18, InputKey.KEYBOARD_F18),
            Map.entry(GLFW_KEY_F19, InputKey.KEYBOARD_F19),
            Map.entry(GLFW_KEY_F20, InputKey.KEYBOARD_F20),
            Map.entry(GLFW_KEY_F21, InputKey.KEYBOARD_F21),
            Map.entry(GLFW_KEY_F22, InputKey.KEYBOARD_F22),
            Map.entry(GLFW_KEY_F23, InputKey.KEYBOARD_F23),
            Map.entry(GLFW_KEY_F24, InputKey.KEYBOARD_F24),

            Map.entry(GLFW_KEY_NUM_LOCK, InputKey.KEYBOARD_NUMLOCK),
            Map.entry(GLFW_KEY_SCROLL_LOCK, InputKey.KEYBOARD_SCROLLLOCK),
            Map.entry(GLFW_KEY_LEFT_SHIFT, InputKey.KEYBOARD_SHIFT_LEFT),
            Map.entry(GLFW_KEY_RIGHT_SHIFT, InputKey.KEYBOARD_SHIFT_RIGHT),
            Map.entry(GLFW_KEY_LEFT_CONTROL, InputKey.KEYBOARD_CTRL_LEFT),
            Map.entry(GLFW_KEY_RIGHT_CONTROL, InputKey.KEYBOARD_CTRL_RIGHT),
            Map.entry(GLFW_KEY_LEFT_ALT, InputKey.KEYBOARD_ALT_LEFT),
            Map.entry(GLFW_KEY_RIGHT_ALT, InputKey.KEYBOARD_ALT_RIGHT),

            Map.entry(GLFW_KEY_SEMICOLON, InputKey.KEYBOARD_SEMICOLON),
            Map.entry(GLFW_KEY_EQUAL, InputKey.KEYBOARD_EQUALS),
            Map.entry(GLFW_KEY_COMMA, InputKey.KEYBOARD_COMMA),
            Map.entry(GLFW_KEY_MINUS, InputKey.KEYBOARD_MINUS),
            Map.entry(GLFW_KEY_PERIOD, InputKey.KEYBOARD_PERIOD),
            Map.entry(GLFW_KEY_SLASH, InputKey.KEYBOARD_SLASH),
            Map.entry(GLFW_KEY_GRAVE_ACCENT, InputKey.KEYBOARD_GRAVE),
            Map.entry(GLFW_KEY_LEFT_BRACKET, InputKey.KEYBOARD_BRACKET_LEFT),
            Map.entry(GLFW_KEY_BACKSLASH, InputKey.KEYBOARD_BACKSLASH),
            Map.entry(GLFW_KEY_RIGHT_BRACKET, InputKey.KEYBOARD_BRACKET_RIGHT),

            Map.entry(GLFW_KEY_MENU, InputKey.KEYBOARD_MENU)
    );

    // </editor-fold>

    private long handle;
    private final Object shouldCloseLock = new Object();
    private String title;
    private Vector2i windowedPos;
    private Vector2i windowedSize;
    private boolean maximized = false;
    private boolean minimized = false;
    private boolean fullscreen = false;
    private Monitor monitor = Monitor.getPrimary();
    private final Vector2i framebufferSizeCache = new Vector2i();
    private Vector2i lastCursorPos = new Vector2i(0);
    private Vector2i nextWarpPos = null;


    private static void keyCallback(long window, int key, int scanCode, int action, int mods) {
        InputKey inputKey = GLFW_KEY_MAP.get(key);
        if(inputKey == null) return;

        KeyInputEvent.Type eventType = KeyInputEvent.Type.ECHO;
        if(action == GLFW_PRESS) eventType = KeyInputEvent.Type.PRESS;
        else if(action == GLFW_RELEASE) eventType = KeyInputEvent.Type.RELEASE;

        Engine.getInstance().getInputManager().enqueueEvent(new KeyInputEvent(eventType, inputKey));
    }

    private static void charCallback(long window, int codePoint) {
        Engine.getInstance().getInputManager().enqueueEvent(new TextInputEvent(codePoint));
    }

    private static void mouseButtonCallback(long window, int button, int action, int mods) {
        InputKey inputKey = GLFW_MOUSE_BUTTON_MAP.get(button);
        if(inputKey == null) return;

        KeyInputEvent.Type eventType = KeyInputEvent.Type.ECHO;
        if(action == GLFW_PRESS) eventType = KeyInputEvent.Type.PRESS;
        else if(action == GLFW_RELEASE) eventType = KeyInputEvent.Type.RELEASE;

        Engine.getInstance().getInputManager().enqueueEvent(new KeyInputEvent(eventType, inputKey));
    }

    private static void scrollCallback(long window, double offsetX, double offsetY) {
        InputManager inputManager = Engine.getInstance().getInputManager();

        long yOffset = Math.round(offsetY);
        inputManager.setAxisValue(InputAxis.MOUSE_WHEEL_Y, yOffset);

        if(yOffset > 0) {
            for(int i = 0; i < yOffset; i++) {
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.PRESS, InputKey.MOUSE_WHEEL_UP));
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.RELEASE, InputKey.MOUSE_WHEEL_UP));
            }
        } else {
            for(int i = 0; i < -yOffset; i++) {
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.PRESS, InputKey.MOUSE_WHEEL_DOWN));
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.RELEASE, InputKey.MOUSE_WHEEL_DOWN));
            }
        }

        // This is untested, the direction may be wrong.
        long xOffset = Math.round(offsetX);
        inputManager.setAxisValue(InputAxis.MOUSE_WHEEL_X, xOffset);

        if(xOffset > 0) {
            for(int i = 0; i < xOffset; i++) {
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.PRESS, InputKey.MOUSE_WHEEL_LEFT));
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.RELEASE, InputKey.MOUSE_WHEEL_LEFT));
            }
        } else {
            for(int i = 0; i < -xOffset; i++) {
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.PRESS, InputKey.MOUSE_WHEEL_RIGHT));
                inputManager.enqueueEvent(new KeyInputEvent(KeyInputEvent.Type.RELEASE, InputKey.MOUSE_WHEEL_RIGHT));
            }
        }
    }

    private void windowPosCallback(long window, int x, int y) {
        // We can't rely on the tracking variables as callbacks may fire in the wrong order
        final boolean maximized = isGlfwMaximized();
        final boolean minimized = isGlfwMinimized();
        if(fullscreen || maximized || minimized) return;
        windowedPos = new Vector2i(x, y);
    }

    private void windowSizeCallback(long window, int width, int height) {
        // We can't rely on the tracking variables as callbacks may fire in the wrong order
        final boolean maximized = isGlfwMaximized();
        final boolean minimized = isGlfwMinimized();
        if(fullscreen || maximized || minimized) return;
        windowedSize = new Vector2i(Math.max(width, 1), Math.max(height, 1));
    }

    private void windowIconifyCallback(long window, boolean iconified) {
        minimized = iconified;
    }

    private void windowMaximizeCallback(long window, boolean maximized) {
        this.maximized = maximized;
    }

    private void framebufferSizeCallback(long window, int width, int height) {
        synchronized(framebufferSizeCache) {
            framebufferSizeCache.setX(Math.max(width, 1));
            framebufferSizeCache.setY(Math.max(height, 1));
        }
    }

    private Vector2i getGlfwCursorPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer posX = stack.mallocDouble(1);
            final DoubleBuffer posY = stack.mallocDouble(1);

            glfwGetCursorPos(handle, posX, posY);

            return new Vector2i((int)posX.get(0), (int)posY.get(0));
        }
    }

    private Vector2i getGlfwPos() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer posX = stack.mallocInt(1);
            final IntBuffer posY = stack.mallocInt(1);

            glfwGetWindowPos(handle, posX, posY);

            return new Vector2i(posX.get(0), posY.get(0));
        }
    }

    private Vector2i getGlfwSize() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer sizeX = stack.mallocInt(1);
            final IntBuffer sizeY = stack.mallocInt(1);

            glfwGetWindowSize(handle, sizeX, sizeY);

            return new Vector2i(sizeX.get(0), sizeY.get(0));
        }
    }

    private boolean isGlfwMaximized() {
        return glfwGetWindowAttrib(handle, GLFW_MAXIMIZED) == GLFW_TRUE;
    }

    private boolean isGlfwMinimized() {
        return glfwGetWindowAttrib(handle, GLFW_ICONIFIED) == GLFW_TRUE;
    }

    private void applyWindowMode() {
        // Minimization overrides maximization, they both override windowed mode:
        if(maximized) glfwMaximizeWindow(handle);
        if(minimized) glfwIconifyWindow(handle);
        if(!maximized && !minimized) glfwRestoreWindow(handle);
    }

    private void warpCursor(Vector2i pos) {
        glfwSetCursorPos(handle, pos.getX(), pos.getY());
        nextWarpPos = pos;
    }

    // Check if the supplied position is the expected warping position and reset the warp position if true
    private boolean testAndResetWarpPos(Vector2i pos) {
        if(nextWarpPos == null) return false;

        if(pos.equals(nextWarpPos)) {
            nextWarpPos = null;
            return true;
        } else
            return false;
    }
}
