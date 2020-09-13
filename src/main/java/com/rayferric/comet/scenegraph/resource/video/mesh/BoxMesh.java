package com.rayferric.comet.scenegraph.resource.video.mesh;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.mesh.MeshData;
import com.rayferric.comet.mesh.MeshGenerator;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.video.recipe.mesh.MeshRecipe;

public class BoxMesh extends Mesh {
    public BoxMesh(Vector3f size, boolean shadeSmooth) {
        this.size = size;
        this.shadeSmooth = shadeSmooth;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            MeshData data = MeshGenerator.genBox(size, shadeSmooth);

            MeshRecipe recipe = new MeshRecipe(null, data);
            serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

            finishLoading();
        });

        return true;
    }

    private final Vector3f size;
    private final boolean shadeSmooth;
}
