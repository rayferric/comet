package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.component.Component;
import com.rayferric.comet.scenegraph.resource.video.buffer.UniformBuffer;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Material implements Component {
    public Shader getShader() {
        return shader.get();
    }

    public void setShader(Shader shader) {
        this.shader.set(shader);
    }

    public UniformBuffer getUniformBuffer() {
        return uniformBuffer;
    }

    /**
     * Returns whether this material was modified from after last call to this method.<br>
     * • Is internally used by the video engine to determine if the uniform buffer needs an update.<br>
     * • Must not be called by the user, this is an internal method.<br>
     *
     * @return true if modified
     */
    public boolean needsUpdate() {
        return modified.compareAndSet(true, false);
    }

    /**
     * Creates a snapshot of the current state of the uniform data
     * and returns it as a {@link FloatBuffer} allocated on the stack.
     *
     * @param stack memory stack
     *
     * @return float buffer
     */
    public FloatBuffer snapUniformData(MemoryStack stack) {
        synchronized(uniformData) {
            uniformData.position(0);
            ByteBuffer nativeData = stack.malloc(uniformData.capacity());
            return nativeData.asFloatBuffer().put(uniformData.asFloatBuffer()).flip();
        }
    }

    public HashMap<Integer, Texture> getTextures() {
        synchronized(textures) {
            return new HashMap<>(textures);
        }
    }

    public boolean hasCulling() {
        return culling.get();
    }

    public void setCulling(boolean culling) {
        this.culling.set(culling);
    }

    public boolean isTranslucent() {
        return translucent.get();
    }

    public void setTranslucent(boolean translucent) {
        this.translucent.set(translucent);
    }

    protected Material(int uniformBufferSize) {
        uniformBuffer = new UniformBuffer(uniformBufferSize);
        uniformData = ByteBuffer.allocate(uniformBufferSize);
    }

    protected static int nextStd140(int prevAddress, int prevBytes, int nextBytes) {
        int offset = prevAddress + prevBytes;

        int baseAlignment = 16;
        if(nextBytes <= 4) baseAlignment = 4;
        else if(nextBytes <= 8) baseAlignment = 8;

        int alignedOffset = offset;
        while(alignedOffset % baseAlignment != 0)
            alignedOffset += 4;

        return alignedOffset;
    }

    protected int readUniformInt(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            return uniformData.asIntBuffer().get();
        }
    }

    protected Vector2i readUniformVector2i(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            IntBuffer tmp = uniformData.asIntBuffer();
            return new Vector2i(tmp.get(), tmp.get());
        }
    }

    protected Vector3i readUniformVector3i(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            IntBuffer tmp = uniformData.asIntBuffer();
            return new Vector3i(tmp.get(), tmp.get(), tmp.get());
        }
    }

    protected Vector4i readUniformVector4i(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            IntBuffer tmp = uniformData.asIntBuffer();
            return new Vector4i(tmp.get(), tmp.get(), tmp.get(), tmp.get());
        }
    }

    protected float readUniformFloat(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            return uniformData.asFloatBuffer().get();
        }
    }

    protected Vector2f readUniformVector2f(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            FloatBuffer tmp = uniformData.asFloatBuffer();
            return new Vector2f(tmp.get(), tmp.get());
        }
    }

    protected Vector3f readUniformVector3f(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            FloatBuffer tmp = uniformData.asFloatBuffer();
            return new Vector3f(tmp.get(), tmp.get(), tmp.get());
        }
    }

    protected Vector4f readUniformVector4f(int address) {
        synchronized(uniformData) {
            uniformData.position(address);
            FloatBuffer tmp = uniformData.asFloatBuffer();
            return new Vector4f(tmp.get(), tmp.get(), tmp.get(), tmp.get());
        }
    }

    protected Matrix4f readUniformMatrix4f(int address) {
        synchronized(uniformData) {
            Vector4f x = readUniformVector4f(address);
            Vector4f y = readUniformVector4f(address += Vector4f.BYTES);
            Vector4f z = readUniformVector4f(address += Vector4f.BYTES);
            Vector4f w = readUniformVector4f(address + Vector4f.BYTES);
            return new Matrix4f(x, y, z, w);
        }
    }

    protected void writeUniformData(int address, int[] values) {
        synchronized(uniformData) {
            uniformData.position(address);
            uniformData.asIntBuffer().put(values);
        }
        modified.set(true);
    }

    protected void writeUniformData(int address, float[] values) {
        synchronized(uniformData) {
            uniformData.position(address);
            uniformData.asFloatBuffer().put(values);
        }
        modified.set(true);
    }

    protected Texture getTexture(int binding) {
        synchronized(textures) {
            return textures.get(binding);
        }
    }

    protected void setTexture(int binding, Texture texture) {
        synchronized(textures) {
            textures.put(binding, texture);
        }
    }

    private AtomicReference<Shader> shader = new AtomicReference<>();
    private final UniformBuffer uniformBuffer;
    private final ByteBuffer uniformData;
    private final AtomicBoolean modified = new AtomicBoolean(true);
    private final HashMap<Integer, Texture> textures = new HashMap<>();
    private final AtomicBoolean culling = new AtomicBoolean(true);
    private final AtomicBoolean translucent = new AtomicBoolean(false);
}
