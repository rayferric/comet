package com.rayferric.comet.core.scenegraph.resource.video.mesh;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.video.recipe.mesh.MeshRecipe;

public class ArrayMesh extends Mesh {
    public ArrayMesh(MeshData data) {
        properties = new Properties();
        properties.data = data;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        MeshRecipe recipe = new MeshRecipe(null, properties.data);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
        finishLoading();

        return true;
    }

    private static class Properties {
        public MeshData data;
    }

    private final Properties properties;
}
