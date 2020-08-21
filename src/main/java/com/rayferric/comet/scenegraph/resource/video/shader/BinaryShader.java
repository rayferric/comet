package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.server.recipe.video.BinaryShaderRecipe;
import com.rayferric.comet.util.ResourceLoader;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BinaryShader extends Shader {
    public BinaryShader(boolean fromJar, String vertPath, String fragPath) {
        super(fromJar, vertPath, fragPath);
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                ByteBuffer vertBin =
                        ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.vertPath);
                ByteBuffer fragBin =
                        ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.fragPath);

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
}
