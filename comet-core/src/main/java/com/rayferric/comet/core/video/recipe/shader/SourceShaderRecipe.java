package com.rayferric.comet.core.video.recipe.shader;

import com.rayferric.comet.core.video.recipe.VideoRecipe;
import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.video.VideoEngine;

public class SourceShaderRecipe extends VideoRecipe {
    public SourceShaderRecipe(Runnable cleanUpCallback, String vertSrc, String fragSrc) {
        super(cleanUpCallback);

        this.vertSrc = vertSrc;
        this.fragSrc = fragSrc;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createSourceShader(vertSrc, fragSrc);
    }

    private final String vertSrc;
    private final String fragSrc;
}
