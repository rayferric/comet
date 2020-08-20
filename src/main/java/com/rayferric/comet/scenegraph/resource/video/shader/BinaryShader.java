package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.server.recipe.video.BinaryShaderRecipe;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BinaryShader extends Shader {
    public BinaryShader(String vertPath, String fragPath) {
        super(vertPath, fragPath);
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                ByteBuffer vertBin = readBinaryFileToNativeBuffer(properties.vertPath);
                ByteBuffer fragBin = readBinaryFileToNativeBuffer(properties.fragPath);

                BinaryShaderRecipe recipe = new BinaryShaderRecipe(() -> {
                    MemoryUtil.memFree(vertBin);
                    MemoryUtil.memFree(fragBin);
                    finishLoading();
                }, vertBin, fragBin);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private ByteBuffer readBinaryFileToNativeBuffer(String path) {
        File file = new File(path);
        ByteBuffer buffer = ByteBuffer.allocate((int)file.length());

        try {
            InputStream stream = new FileInputStream(file);
            stream.read(buffer.array());
            stream.close();
        } catch(Exception e) {
            throw new RuntimeException("Failed to read file.\n" + e.getMessage());
        }

        ByteBuffer nativeBuffer = MemoryUtil.memAlloc(buffer.capacity());
        nativeBuffer.put(buffer);
        nativeBuffer.flip();

        return nativeBuffer;
    }
}
