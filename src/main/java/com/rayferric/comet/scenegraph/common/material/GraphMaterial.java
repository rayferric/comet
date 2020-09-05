package com.rayferric.comet.scenegraph.common.material;

import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;

public class GraphMaterial extends Material {
    public GraphMaterial() {
        super(0);

        synchronized(GRAPH_SHADER_LOCK) {
            if(graphShader == null)
                graphShader = new BinaryShader(true, "shaders/graph.vert.spv", "shaders/graph.frag.spv");
        }
        graphShader.load();

        setShader(graphShader);
    }

    private static final Object GRAPH_SHADER_LOCK = new Object();
    private static Shader graphShader = null;
}
