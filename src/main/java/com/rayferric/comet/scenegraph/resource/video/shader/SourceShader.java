package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.server.recipe.video.SourceShaderRecipe;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SourceShader extends Shader {
    public SourceShader(String vertPath, String fragPath) {
        super(vertPath, fragPath);
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                String vertSrc = new String(Files.readAllBytes(Paths.get(properties.vertPath)));
                String fragSrc = new String(Files.readAllBytes(Paths.get(properties.fragPath)));

                SourceShaderRecipe recipe = new SourceShaderRecipe(this::finishLoading, vertSrc, fragSrc);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
