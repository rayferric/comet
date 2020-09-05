package com.rayferric.comet.scenegraph.resource.video.geometry;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.video.recipe.geometry.GeometryRecipe;

public class BoxGeometry extends Geometry {
    public BoxGeometry(Vector3f size, boolean shadeSmooth) {
        this.size = size;
        this.shadeSmooth = shadeSmooth;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            GeometryData data = GeometryGenerator.genBox(size, shadeSmooth);

            GeometryRecipe recipe = new GeometryRecipe(null, data);
            serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

            finishLoading();
        });

        return true;
    }

    private final Vector3f size;
    private final boolean shadeSmooth;
}
