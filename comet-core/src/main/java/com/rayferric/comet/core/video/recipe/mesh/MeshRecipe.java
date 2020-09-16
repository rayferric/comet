package com.rayferric.comet.core.video.recipe.mesh;

import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.video.recipe.VideoRecipe;
import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.video.VideoEngine;

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
