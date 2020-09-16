package com.rayferric.comet.core.scenegraph.resource.video.shader;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.video.recipe.shader.SourceShaderRecipe;
import com.rayferric.comet.core.util.ResourceLoader;

public class SourceShader extends Shader {
    public SourceShader(boolean fromJar, String vertPath, String fragPath) {
        super(fromJar, vertPath, fragPath);
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                String vertSrc = ResourceLoader.readTextFileToString(properties.fromJar, properties.vertPath);
                String fragSrc = ResourceLoader.readTextFileToString(properties.fromJar, properties.fragPath);

                SourceShaderRecipe recipe = new SourceShaderRecipe(null, vertSrc, fragSrc);
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
