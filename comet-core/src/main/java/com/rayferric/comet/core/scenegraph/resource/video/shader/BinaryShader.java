package com.rayferric.comet.core.scenegraph.resource.video.shader;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.video.recipe.shader.BinaryShaderRecipe;
import com.rayferric.comet.core.util.ResourceLoader;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BinaryShader extends Shader {
    public BinaryShader(boolean fromJar, String vertPath, String fragPath) {
        super(fromJar, vertPath, fragPath);
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                ByteBuffer vertBin =
                        ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.vertPath);
                ByteBuffer fragBin =
                        ResourceLoader.readBinaryFileToNativeBuffer(properties.fromJar, properties.fragPath);

                BinaryShaderRecipe recipe = new BinaryShaderRecipe(() -> {
                    MemoryUtil.memFree(vertBin);
                    MemoryUtil.memFree(fragBin);
                }, vertBin, fragBin);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

                finishLoading();
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        return true;
    }
}
