package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.server.recipe.video.ShaderRecipe;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class Shader extends VideoResource {
    public Shader(String vertPath, String fragPath) {
        properties = new Properties();
        properties.vertPath = vertPath;
        properties.fragPath = fragPath;

        load();
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                ByteBuffer vertData = readBinaryFileToNativeBuffer(properties.vertPath);
                ByteBuffer fragData = readBinaryFileToNativeBuffer(properties.fragPath);

                ShaderRecipe recipe = new ShaderRecipe(this::finishLoading, vertData, fragData);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    @Override
    public void unload() {
        super.unload();

        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle.get());
    }

    public long getServerHandle() {
        return serverHandle.get();
    }

    private static class Properties {
        public String vertPath, fragPath;
    }

    private final Properties properties;
    private final AtomicLong serverHandle = new AtomicLong();

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

        ByteBuffer nativeBuffer = BufferUtils.createByteBuffer(buffer.capacity());
        nativeBuffer.put(buffer);
        nativeBuffer.flip();

        return nativeBuffer;
    }
}
