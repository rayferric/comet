package com.rayferric.comet.core.video.recipe.buffer;

import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.video.VideoEngine;
import com.rayferric.comet.core.video.recipe.VideoRecipe;

public class UniformBufferRecipe extends VideoRecipe {
    public UniformBufferRecipe(int size) {
        super(null);

        this.size = size;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createUniformBuffer(size);
    }

    private final int size;
}
