package com.rayferric.comet.video.recipe.mesh;

import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshRecipe extends VideoRecipe {
    public MeshRecipe(Runnable cleanUpCallback, FloatBuffer vertices, IntBuffer indices) {
        super(cleanUpCallback);

        this.vertices = vertices;
        this.indices = indices;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createMesh(vertices, indices);
    }

    private final FloatBuffer vertices;
    private final IntBuffer indices;
}
