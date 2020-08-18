package com.rayferric.comet.video.gl.texture;

import com.rayferric.comet.scenegraph.resource.video.ImageTexture;

import static org.lwjgl.opengl.GL45.*;

public class GLTexture2DImage extends GLTexture2D {
    public GLTexture2DImage(ImageTexture.ServerRecipe recipe) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, recipe.getChannels() == 4 ? GL_RGBA8 : GL_RGB8, recipe.getWidth(),
                recipe.getHeight(), 0, recipe.getChannels() == 4 ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE,
                recipe.getData());

        glGenerateMipmap(GL_TEXTURE_2D);
    }
}
