package com.rayferric.comet.scenegraph.resource.video.geometry;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.geometry.GeometryData;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.text.FontMetadata;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;
import com.rayferric.comet.video.recipe.geometry.GeometryRecipe;

import java.util.concurrent.RejectedExecutionException;

public class TextGeometry extends Geometry {
    public TextGeometry(String text, FontMetadata meta, HorizontalAlignment hAlign,
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
                GeometryData data = GeometryGenerator
                        .genText(properties.text, properties.meta, properties.hAlign, properties.vAlign,
                                properties.autoWrap, properties.wrapSize, properties.charSpacing,
                                properties.lineSpacing);

                GeometryRecipe recipe = new GeometryRecipe(null, data);
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
