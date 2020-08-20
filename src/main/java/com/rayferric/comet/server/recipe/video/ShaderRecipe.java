package com.rayferric.comet.server.recipe.video;

import com.rayferric.comet.server.recipe.ServerRecipe;

import java.nio.ByteBuffer;

public class ShaderRecipe extends ServerRecipe {
    public ShaderRecipe(Runnable cleanUpCallback, ByteBuffer vertData, ByteBuffer fragData) {
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
