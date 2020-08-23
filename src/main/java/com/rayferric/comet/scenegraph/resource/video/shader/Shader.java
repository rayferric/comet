package com.rayferric.comet.scenegraph.resource.video.shader;

import com.rayferric.comet.Engine;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Shader extends VideoResource {
    public Shader(boolean fromJar, String vertPath, String fragPath) {
        properties = new Properties();
        properties.fromJar = fromJar;
        properties.vertPath = vertPath;
        properties.fragPath = fragPath;

        load();
    }

    protected static class Properties {
        public boolean fromJar;
        public String vertPath, fragPath;
    }

    protected final Properties properties;
}
