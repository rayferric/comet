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
    public boolean load() {
        if(!super.load()) return false;

        UniformBufferRecipe recipe = new UniformBufferRecipe(null, properties.size);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
        finishLoading();

        return true;
    }

    private static class Properties {
        public int size;
    }

    private final Properties properties;
}
