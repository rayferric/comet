package com.rayferric.comet.core.video.util;

import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.math.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Monitor {
    @Override
    public String toString() {
        return String.format("Monitor{handle=%s}", handle);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Monitor other = (Monitor)o;
        return handle == other.handle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(handle);
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

    public Vector2i getResolution() {
        return new Vector2i(mode.width(), mode.height());
    }

    public Vector3i getColorDepth() {
        return new Vector3i(mode.redBits(), mode.greenBits(), mode.blueBits());
    }

    public int getRefreshRate() {
        return mode.refreshRate();
    }

    public long getHandle() {
        return handle;
    }

    private Monitor(long handle) {
        this.handle = handle;
        this.mode = glfwGetVideoMode(handle);
        if(mode == null)
            throw new RuntimeException("Failed to acquire video mode of a monitor.");
    }

    private final long handle;
    private final GLFWVidMode mode;
}
