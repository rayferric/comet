package com.rayferric.comet.video.recipe.shader;

import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;

import java.nio.ByteBuffer;

public class BinaryShaderRecipe extends VideoRecipe {
    public BinaryShaderRecipe(Runnable cleanUpCallback, ByteBuffer vertBin, ByteBuffer fragBin) {
        super(cleanUpCallback);

        this.vertBin = vertBin;
        this.fragBin = fragBin;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createBinaryShader(vertBin, fragBin);
    }

    private final ByteBuffer vertBin;
    private final ByteBuffer fragBin;
}
