package com.rayferric.comet.core.scenegraph.resource.video.mesh;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.mesh.MeshGenerator;
import com.rayferric.comet.core.math.Vector2f;
import com.rayferric.comet.core.video.recipe.mesh.MeshRecipe;

public class PlaneMesh extends Mesh {
    public PlaneMesh(Vector2f size) {
        properties = new Properties();
        properties.size = size;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            MeshData data = MeshGenerator.genPlane(properties.size);

            MeshRecipe recipe = new MeshRecipe(null, data);
            serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

            finishLoading();
        });

        return true;
    }

    private static class Properties {
        public Vector2f size;
    }

    private final Properties properties;
}
