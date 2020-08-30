package com.rayferric.comet.scenegraph.resource.video.geometry;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.video.recipe.geometry.GeometryRecipe;

public class GraphGeometry extends Geometry {
    public GraphGeometry(float[] values) {
        properties = new Properties();
        properties.values = values;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                GeometryData data = GeometryGenerator.genGraph(properties.values);
                GeometryRecipe recipe = new GeometryRecipe(null, data);
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
