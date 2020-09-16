package com.rayferric.comet.core.scenegraph.resource.video.buffer;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.core.video.recipe.buffer.UniformBufferRecipe;

public class UniformBuffer extends VideoResource {
    public UniformBuffer(int size) {
        properties = new Properties();
        properties.size = size;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        UniformBufferRecipe recipe = new UniformBufferRecipe(properties.size);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
        finishLoading();

        return true;
    }

    private static class Properties {
        public int size;
    }

    private final Properties properties;
}
