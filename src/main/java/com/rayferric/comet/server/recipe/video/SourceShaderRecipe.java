package com.rayferric.comet.server.recipe.video;

import com.rayferric.comet.server.recipe.ServerRecipe;

public class SourceShaderRecipe extends ServerRecipe {
    public SourceShaderRecipe(Runnable cleanUpCallback, String vertSrc, String fragSrc) {
        super(cleanUpCallback);

        this.vertSrc = vertSrc;
        this.fragSrc = fragSrc;
    }

    public String getVertSrc() {
        return vertSrc;
    }

    public String getFragSrc() {
        return fragSrc;
    }

    private final String vertSrc;
    private final String fragSrc;
}
