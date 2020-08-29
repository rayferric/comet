package com.rayferric.comet.video.api.gl.query;

import com.rayferric.comet.server.ServerResource;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL43.*;

public class GLTimerQuery implements ServerResource {
    public GLTimerQuery() {
        handle = glGenQueries();
        glBeginQuery(GL_TIME_ELAPSED, handle);
        glEndQuery(GL_TIME_ELAPSED);
    }

    @Override
    public void destroy() {
        glDeleteQueries(handle);
    }

    public boolean hasResult() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buf = stack.mallocInt(1);
            glGetQueryObjectiv(handle, GL_QUERY_RESULT_AVAILABLE, buf);
            return (buf.get(0) == GL_TRUE);
        }
    }

    public double read() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer buf = stack.mallocLong(1);
            glGetQueryObjectui64v(handle, GL_QUERY_RESULT, buf);
            return buf.get(0) * INVERSE_FREQUENCY;
        }
    }

    public void begin() {
        glBeginQuery(GL_TIME_ELAPSED, handle);
        recording = true;
    }

    public void end() {
        if(recording) {
            glEndQuery(GL_TIME_ELAPSED);
            recording = false;
        }
    }

    private static final double INVERSE_FREQUENCY = 1e-9;

    private final int handle;
    private boolean recording;
}
