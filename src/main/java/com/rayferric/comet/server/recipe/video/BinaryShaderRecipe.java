package com.rayferric.comet.server.recipe.video;

import com.rayferric.comet.server.recipe.ServerRecipe;

import java.nio.ByteBuffer;

public class BinaryShaderRecipe extends ServerRecipe {
    public BinaryShaderRecipe(Runnable cleanUpCallback, ByteBuffer vertBin, ByteBuffer fragBin) {
        super(cleanUpCallback);

        this.vertBin = vertBin;
        this.fragBin = fragBin;
    }

    public ByteBuffer getVertBin() {
        return vertBin;
    }

    public ByteBuffer getFragBin() {
        return fragBin;
    }

    private final ByteBuffer vertBin;
    private final ByteBuffer fragBin;
}
