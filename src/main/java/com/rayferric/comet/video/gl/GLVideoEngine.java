package com.rayferric.comet.video.gl;

import com.rayferric.comet.resources.Texture;
import com.rayferric.comet.video.common.InternalVideoResource;
import com.rayferric.comet.video.common.VideoEngine;

public class GLVideoEngine extends VideoEngine {
    public GLVideoEngine(String wndTitle, int wndWidth, int wndHeight) {
        super(wndTitle, wndWidth, wndHeight);
    }

    @Override
    public void draw() {
        System.out.println("Drawing");
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected InternalVideoResource createTexture(Texture.InternalRecipe recipe) {
        return new GLTexture(recipe);
    }
}
