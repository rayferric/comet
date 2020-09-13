package com.rayferric.comet.video.recipe.mesh;

import com.rayferric.comet.mesh.MeshData;
import com.rayferric.comet.video.recipe.VideoRecipe;
import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.video.VideoEngine;

public class MeshRecipe extends VideoRecipe {
    public MeshRecipe(Runnable cleanUpCallback, MeshData data) {
        super(cleanUpCallback);

        this.data = data;
    }

    @Override
    public ServerResource resolve(VideoEngine videoEngine) {
        return videoEngine.createMesh(data);
    }

    private final MeshData data;
}
