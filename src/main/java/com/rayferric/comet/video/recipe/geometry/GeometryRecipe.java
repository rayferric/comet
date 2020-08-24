package com.rayferric.comet.video.recipe.geometry;

import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GeometryRecipe extends VideoRecipe {
    public GeometryRecipe(Runnable cleanUpCallback, GeometryData data) {
        super(cleanUpCallback);

        this.data = data;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createGeometry(data);
    }

    private final GeometryData data;
}
