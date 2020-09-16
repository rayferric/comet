package com.rayferric.comet.core.scenegraph.resource.scene;

import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.resource.Resource;

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
