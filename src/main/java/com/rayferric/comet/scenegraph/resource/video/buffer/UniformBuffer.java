package com.rayferric.comet.scenegraph.resource.video.buffer;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.video.recipe.buffer.UniformBufferRecipe;

public class UniformBuffer extends VideoResource {
    public UniformBuffer(int size) {
        properties = new Properties();
        properties.size = size;

        load();
    }

    @Override
    public void load() {
        super.load();

        UniformBufferRecipe recipe = new UniformBufferRecipe(this::finishLoading, properties.size);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
    }

    private static class Properties {
        public int size;
    }

    private final Properties properties;
}
