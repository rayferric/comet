package com.rayferric.comet.core.scenegraph.resource.video.mesh;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.mesh.MeshData;
import com.rayferric.comet.core.mesh.MeshGenerator;
import com.rayferric.comet.core.text.FontMetadata;
import com.rayferric.comet.core.text.HorizontalAlignment;
import com.rayferric.comet.core.text.VerticalAlignment;
import com.rayferric.comet.core.video.recipe.mesh.MeshRecipe;

public class TextMesh extends Mesh {
    public TextMesh(String text, FontMetadata meta, HorizontalAlignment hAlign,
                    VerticalAlignment vAlign, boolean autoWrap, float wrapSize, float charSpacing,
                    float lineSpacing) {
        properties = new Properties();
        properties.text = text;
        properties.meta = meta;
        properties.hAlign = hAlign;
        properties.vAlign = vAlign;
        properties.autoWrap = autoWrap;
        properties.wrapSize = wrapSize;
        properties.charSpacing = charSpacing;
        properties.lineSpacing = lineSpacing;

        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        Engine.getInstance().getLoaderPool().execute(() -> {
            try {
                MeshData data = MeshGenerator
                        .genText(properties.text, properties.meta, properties.hAlign, properties.vAlign,
                                properties.autoWrap, properties.wrapSize, properties.charSpacing,
                                properties.lineSpacing);

                MeshRecipe recipe = new MeshRecipe(null, data);
                serverHandle.set(Engine.getInstance().getVideoServer().scheduleResourceCreation(recipe));

                finishLoading();
            } catch(Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        return true;
    }

    private static class Properties {
        public String text;
        public FontMetadata meta;
        public HorizontalAlignment hAlign;
        public VerticalAlignment vAlign;
        public boolean autoWrap;
        public float wrapSize;
        public float charSpacing;
        public float lineSpacing;
    }

    private final Properties properties;
}
