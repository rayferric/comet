package com.rayferric.comet.scenegraph.resource.video.geometry;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.video.recipe.geometry.GeometryRecipe;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PlaneGeometry extends Geometry {
    public PlaneGeometry(Vector2f size) {
        properties = new Properties();
        properties.size = size;

        load();
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            GeometryData data = GeometryGenerator.genPlane(properties.size);

            GeometryRecipe recipe = new GeometryRecipe(null, data);
            serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

            finishLoading();
        });
    }

    private static class Properties {
        public Vector2f size;
    }

    private final Properties properties;
}
