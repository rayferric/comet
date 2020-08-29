package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.geometry.GeometryGenerator;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.component.material.FontMaterial;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.scenegraph.resource.video.geometry.ArrayGeometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;
import com.rayferric.comet.util.AtomicFloat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Label extends Node {
    public Label() {
        setName("Label");
    }

    @Override
    public void indexAll(LayerIndex index) {
        index.add(this);
        super.indexAll(index);
    }

    public String getText() {
        return text.get();
    }

    public synchronized void setText(String text) {
        this.text.set(text);
        triggerUpdate();
    }

    public Font getFont() {
        return font.get();
    }

    public synchronized void setFont(Font font) {
        this.font.set(font);
        triggerUpdate();
    }

    public Vector4f getColor() {
        return material.getColor();
    }

    public void setColor(Vector4f color) {
        material.setColor(color);
    }

    public float getCutoff() {
        return material.getCutoff();
    }

    public void setCutoff(float cutoff) {
        material.setCutoff(cutoff);
    }

    public float getSoftness() {
        return material.getSoftness();
    }

    public void setSoftness(float softness) {
        material.setSoftness(softness);
    }

    public boolean getShowBounds() {
        return material.getShowBounds();
    }

    public void setShowBounds(boolean show) {
        material.setShowBounds(show);
    }

    public HorizontalAlignment getHAlign() {
        return hAlign.get();
    }

    public synchronized void setHAlign(HorizontalAlignment alignment) {
        hAlign.set(alignment);
        triggerUpdate();
    }

    public VerticalAlignment getVAlign() {
        return vAlign.get();
    }

    public synchronized void setVAlign(VerticalAlignment alignment) {
        vAlign.set(alignment);
        triggerUpdate();
    }

    public boolean getAutoWrap() {
        return autoWrap.get();
    }

    public synchronized void setAutoWrap(boolean enabled) {
        autoWrap.set(enabled);
        triggerUpdate();
    }

    public float getWrapSize() {
        return wrapSize.get();
    }

    public synchronized void setWrapSize(float size) {
        wrapSize.set(size);
        triggerUpdate();
    }

    public float getCharSpacing() {
        return charSpacing.get();
    }

    public synchronized void setCharSpacing(float spacing) {
        charSpacing.set(spacing);
        triggerUpdate();
    }

    public float getLineSpacing() {
        return lineSpacing.get();
    }

    public synchronized void setLineSpacing(float spacing) {
        lineSpacing.set(spacing);
        triggerUpdate();
    }

    public synchronized Material getMaterial() {
        if(needsUpdate) attemptUpdate();
        return material;
    }

    public synchronized Geometry getGeometry() {
        if(needsUpdate) attemptUpdate();
        return geometry;
    }

    private final AtomicReference<String> text = new AtomicReference<>("");
    private final AtomicReference<Font> font = new AtomicReference<>(null);
    private final FontMaterial material = new FontMaterial();
    private Geometry geometry = null;
    private boolean needsUpdate = true;
    private final AtomicReference<HorizontalAlignment> hAlign = new AtomicReference<>(HorizontalAlignment.LEFT);
    private final AtomicReference<VerticalAlignment> vAlign = new AtomicReference<>(VerticalAlignment.TOP);
    private final AtomicBoolean autoWrap = new AtomicBoolean(false);
    private final AtomicFloat wrapSize = new AtomicFloat(0);
    private final AtomicFloat charSpacing = new AtomicFloat(1);
    private final AtomicFloat lineSpacing = new AtomicFloat(1);

    private void triggerUpdate() {
        needsUpdate = true;
        attemptUpdate();
    }

    private void attemptUpdate() {
        if(geometry != null) geometry.unload();
        Font font = getFont();
        if(font != null && font.isLoaded()) {
            material.setAtlas(font.getAtlas());
            geometry = new ArrayGeometry(GeometryGenerator
                    .genText(getText(), font.getMetadata(), getHAlign(), getVAlign(), getAutoWrap(), getWrapSize(),
                            getCharSpacing(), getLineSpacing()));
            needsUpdate = false;
        } else
            geometry = null;
    }
}
