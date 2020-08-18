package com.rayferric.comet.video;

import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.server.ServerResource;

public interface VideoEngine {
    void init();

    void draw();

    ServerResource createTexture(Texture.InternalRecipe recipe);
}
