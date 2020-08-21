package com.rayferric.comet.scenegraph.resource.video.material;

import com.rayferric.comet.scenegraph.resource.Resource;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;

public abstract class Material extends Resource {
    public Shader getShader() {
        return shader;
    }

    protected Shader shader;

    protected Material() {
        load();
        finishLoading(); // TODO Make finishLoading a part of AsyncResource instead
    }

    protected void setShader(Shader shader) {
        this.shader = shader;
    }
}
