package com.rayferric.comet.scenegraph.resource.video.mesh;

import com.rayferric.comet.Engine;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.server.recipe.video.MeshRecipe;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PlaneMesh extends Mesh {
    public PlaneMesh(Vector2f size) {
        properties = new Properties();
        properties.size = size;

        load();
    }

    @Override
    public void load() {
        super.load();

        Engine.getInstance().getLoaderPool().execute(() -> {
            float sx = properties.size.getX() * 0.5F;
            float sy = properties.size.getY() * 0.5F;

            float[] vertices = {
                    -sx, -sy, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                     sx, -sy, 0, 1, 0, 0, 0, 1, 0, 0, 0,
                     sx,  sy, 0, 1, 1, 0, 0, 1, 0, 0, 0,
                    -sx,  sy, 0, 0, 1, 0, 0, 1, 0, 0, 0
            };

            int[] indices = {
                    0, 1, 2,
                    2, 3, 0
            };

            FloatBuffer verticesNative = MemoryUtil.memAllocFloat(vertices.length);
            verticesNative.put(vertices);
            verticesNative.flip();

            IntBuffer indicesNative = MemoryUtil.memAllocInt(indices.length);
            indicesNative.put(indices);
            indicesNative.flip();

            MeshRecipe recipe = new MeshRecipe(() -> {
                MemoryUtil.memFree(verticesNative);
                MemoryUtil.memFree(indicesNative);
                finishLoading();
            }, verticesNative, indicesNative);
            serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));
        });
    }

    private static class Properties {
        public Vector2f size;
    }

    private final Properties properties;
}
