package com.rayferric.comet.video.common;

import com.rayferric.comet.math.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Monitor {
    @Override
    public String toString() {
        return String.format("Monitor{handle=%s}", handle);
    }

    public static Monitor getPrimary() {
        long monitor = glfwGetPrimaryMonitor();

        if(monitor != NULL)
            return new Monitor(monitor);
        else
            return null;
    }

    public static List<Monitor> queryMonitors() {
        final List<Monitor> monitors = new ArrayList<>();

        PointerBuffer glfwMonitors = glfwGetMonitors();
        if(glfwMonitors != null)
            for(int i = 0; i < glfwMonitors.limit(); i++)
                monitors.add(new Monitor(glfwMonitors.get(i)));

        return monitors;
    }

    public long getHandle() {
        return handle;
    }

    public Vector2i getResolution() {
        GLFWVidMode mode = glfwGetVideoMode(handle);
        if(mode == null)
            throw new NullPointerException();

        return new Vector2i(mode.width(), mode.height());
    }

    private Monitor(long handle) {
        this.handle = handle;
    }

    private final long handle;
}
