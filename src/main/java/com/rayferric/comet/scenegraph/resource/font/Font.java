package com.rayferric.comet.scenegraph.resource.font;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.text.FontMetadata;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.texture.ImageTexture;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;
import com.rayferric.comet.util.ResourceLoader;

public class Font extends Resource {
    public Font(boolean fromJar, String path) {
        properties = new Properties();
        properties.fromJar = fromJar;
        properties.path = path;

        load();
    }

    @Override
    public void load() {
        super.load();

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
