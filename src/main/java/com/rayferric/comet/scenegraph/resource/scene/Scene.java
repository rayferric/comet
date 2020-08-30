package com.rayferric.comet.scenegraph.resource.scene;

import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;

import java.util.List;

public abstract class Scene extends Resource {
    public Scene(String path) {
        properties = new Properties();
        properties.path = path;

        load();
    }

    public abstract Node instantiate();

    protected static class Properties {
        public String path;
    }

    protected final Properties properties;
}
