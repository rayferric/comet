package com.rayferric.comet.core.scenegraph.resource.video.mesh;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.mesh.MeshGenerator;
import com.rayferric.comet.core.video.recipe.mesh.MeshRecipe;

public class GraphMesh extends Mesh {
    public GraphMesh(float[] values) {
        properties = new Properties();
        properties.values = values;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                MeshData data = MeshGenerator.genGraph(properties.values);
                MeshRecipe recipe = new MeshRecipe(null, data);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

                finishLoading();
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        return true;
    }

    private static class Properties {
        public float[] values;
    }

    private final Properties properties;
}
