package com.rayferric.comet.video.recipe;

import com.rayferric.comet.server.ServerResource;
import com.rayferric.comet.server.ServerRecipe;
import com.rayferric.comet.video.VideoEngine;

public abstract class VideoRecipe extends ServerRecipe {
    public VideoRecipe(Runnable cleanUpCallback) {
        super(cleanUpCallback);
    }

    public abstract ServerResource resolve(VideoEngine videoEngine);
}
