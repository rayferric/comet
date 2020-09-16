package com.rayferric.comet.core.scenegraph.resource.font;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.text.FontMetadata;
import com.rayferric.comet.core.scenegraph.resource.Resource;
import com.rayferric.comet.core.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.core.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.core.util.ResourceLoader;

public class Font extends Resource {
    public Font(boolean fromJar, String path) {
        properties = new Properties();
        properties.fromJar = fromJar;
        properties.path = path;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;
        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                String contents = ResourceLoader.readTextFileToString(properties.fromJar, properties.path);
                metadata = new FontMetadata(contents, properties.path);
                atlas = new ImageTexture(properties.fromJar, metadata.getAtlasPath(), true);

                finishLoading();
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
        return true;
    }

    @Override
    public boolean unload() {
        if(!super.unload()) return false;
        atlas.unload();
        return true;
    }

    public FontMetadata getMetadata() {
        if(!isLoaded())
            throw new IllegalStateException("Attempted to query metadata of an unloaded font.");
        return metadata;
    }

    public Texture getAtlas() {
        if(!isLoaded())
            throw new IllegalStateException("Attempted to query atlas texture of an unloaded font.");
        return atlas;
    }

    private static class Properties {
        public boolean fromJar;
        public String path;
    }

    private final Properties properties;
    private FontMetadata metadata;
    private Texture atlas;
}
