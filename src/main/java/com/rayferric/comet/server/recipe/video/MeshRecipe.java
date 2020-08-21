package com.rayferric.comet.server.recipe.video;

import com.rayferric.comet.server.recipe.ServerRecipe;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshRecipe extends ServerRecipe {
    public MeshRecipe(Runnable cleanUpCallback, FloatBuffer vertices, IntBuffer indices) {
        super(cleanUpCallback);

        this.vertices = vertices;
        this.indices = indices;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public IntBuffer getIndices() {
        return indices;
    }

    private final FloatBuffer vertices;
    private final IntBuffer indices;
}
