package com.rayferric.comet.scenegraph.resource.video.geometry;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.video.recipe.geometry.GeometryRecipe;

public class ArrayGeometry extends Geometry {
    public ArrayGeometry(GeometryData data) {
        properties = new Properties();
        properties.data = data;

        load();
    }

    @Override
    public void load() {
        super.load();

        GeometryRecipe recipe = new GeometryRecipe(null, properties.data);
        serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

        finishLoading();
    }

    private static class Properties {
        public GeometryData data;
    }

    private final Properties properties;
}
