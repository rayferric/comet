package com.rayferric.comet.core.video.recipe;

import com.rayferric.comet.core.server.ServerResource;
import com.rayferric.comet.core.server.ServerRecipe;
import com.rayferric.comet.core.video.VideoEngine;

public abstract class VideoRecipe extends ServerRecipe {
    public VideoRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(VideoEngine videoEngine);
}
