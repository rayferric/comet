package com.rayferric.comet.video.gl.texture;

import static org.lwjgl.opengl.GL45.*;

public abstract class GLTexture2D extends GLTexture {
    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }
}
