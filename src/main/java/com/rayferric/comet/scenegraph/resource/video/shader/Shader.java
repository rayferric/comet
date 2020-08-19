package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;

public class Shader extends VideoResource {
    public static class ServerRecipe extends Resource.ServerRecipe {
        public ServerRecipe(Runnable cleanUpCallback, ByteBuffer vertData, ByteBuffer fragData) {
            super(cleanUpCallback);

            this.vertData = vertData;
            this.fragData = fragData;
        }

        public ByteBuffer getVertData() {
            return vertData;
        }

        public ByteBuffer getFragData() {
            return fragData;
        }

        private final ByteBuffer vertData;
        private final ByteBuffer fragData;
    }

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

                ServerRecipe recipe = new ServerRecipe(this::markAsReady, vertData, fragData);
                serverHandle = Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe);
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    @Override
    public void unload() {
        super.unload();
        Engine.getInstance().getVideoServer().scheduleResourceDestruction(serverHandle);
    }

    public long getServerHandle() {
        return serverHandle;
    }

    private static class Properties {
        public String vertPath, fragPath;
    }

    private final Properties properties;
    private long serverHandle;

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
