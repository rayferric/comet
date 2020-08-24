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
     * Creates copy of the current state of the uniform data allocated on a stack
     * and returns it as a native {@link FloatBuffer}.
     *
     * @param stack native memory stack
     *
     * @return native float buffer
     */
    public FloatBuffer nativeUniformData(MemoryStack stack) {
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

    protected Material(int uniformBufferSize) {
        uniformBuffer = new UniformBuffer(uniformBufferSize);
        uniformData = ByteBuffer.allocate(uniformBufferSize);
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
    }

    protected void writeUniformData(int address, float[] values) {
        synchronized(uniformData) {
            uniformData.position(address);
            uniformData.asFloatBuffer().put(values);
        }
    }

    protected Texture getTexture(int binding) {
        synchronized(textures) {
            return textures.get(binding);
        }
    }

    protected void setTexture(int binding, Texture texture) {
        synchronized(textures) {
            if(texture == null)
                textures.remove(binding);
            else
                textures.put(binding, texture);
        }
    }

    private AtomicReference<Shader> shader = new AtomicReference<>();
    private final UniformBuffer uniformBuffer;
    private final ByteBuffer uniformData;
    private final HashMap<Integer, Texture> textures = new HashMap<>();
}
