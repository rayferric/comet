package com.rayferric.comet.scenegraph.component.material;

import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.resource.video.shader.BinaryShader;
import com.rayferric.comet.scenegraph.resource.video.shader.Shader;
import com.rayferric.comet.scenegraph.resource.video.shader.SourceShader;
import com.rayferric.comet.scenegraph.resource.video.texture.Texture;

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
