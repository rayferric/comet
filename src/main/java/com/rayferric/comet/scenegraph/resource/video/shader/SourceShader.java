package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.video.recipe.shader.SourceShaderRecipe;
import com.rayferric.comet.util.ResourceLoader;

public class SourceShader extends Shader {
    public SourceShader(boolean fromJar, String vertPath, String fragPath) {
        super(fromJar, vertPath, fragPath);
    }

    @Override
    public void load() {
        super.load();

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
    }
}
