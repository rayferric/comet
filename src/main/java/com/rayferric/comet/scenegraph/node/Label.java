package com.rayferric.comet.scenegraph.node;

import com.rayferric.comet.engine.LayerIndex;
import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.component.material.FontMaterial;
import com.rayferric.comet.scenegraph.component.material.Material;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.scenegraph.resource.video.geometry.Geometry;
import com.rayferric.comet.scenegraph.resource.video.geometry.TextGeometry;
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

    public void setText(String text) {
        this.text.set(text);
        dispatchUpdate();
    }

    public Font getFont() {
        return font.get();
    }

    public void setFont(Font font) {
        this.font.set(font);
        dispatchUpdate();
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

    public void setHAlign(HorizontalAlignment alignment) {
        hAlign.set(alignment);
        dispatchUpdate();
    }

    public VerticalAlignment getVAlign() {
        return vAlign.get();
    }

    public void setVAlign(VerticalAlignment alignment) {
        vAlign.set(alignment);
        dispatchUpdate();
    }

    public boolean getAutoWrap() {
        return autoWrap.get();
    }

    public void setAutoWrap(boolean enabled) {
        autoWrap.set(enabled);
        dispatchUpdate();
    }

    public float getWrapSize() {
        return wrapSize.get();
    }

    public void setWrapSize(float size) {
        wrapSize.set(size);
        dispatchUpdate();
    }

    public float getCharSpacing() {
        return charSpacing.get();
    }

    public void setCharSpacing(float spacing) {
        charSpacing.set(spacing);
        dispatchUpdate();
    }

    public float getLineSpacing() {
        return lineSpacing.get();
    }

    public void setLineSpacing(float spacing) {
        lineSpacing.set(spacing);
        dispatchUpdate();
    }

    public Material getMaterial() {
        return material;
    }

    public Geometry getGeometry() {
        processUpdates();
        return frontGeometry;
    }

    private final FontMaterial material = new FontMaterial();
    private Geometry frontGeometry = null, backGeometry = null;
    private boolean needsUpdate = false;
    private final AtomicReference<String> text = new AtomicReference<>("");
    private final AtomicReference<Font> font = new AtomicReference<>(null);
    private final AtomicReference<HorizontalAlignment> hAlign = new AtomicReference<>(HorizontalAlignment.LEFT);
    private final AtomicReference<VerticalAlignment> vAlign = new AtomicReference<>(VerticalAlignment.TOP);
    private final AtomicBoolean autoWrap = new AtomicBoolean(false);
    private final AtomicFloat wrapSize = new AtomicFloat(0);
    private final AtomicFloat charSpacing = new AtomicFloat(1);
    private final AtomicFloat lineSpacing = new AtomicFloat(1);

    private synchronized void dispatchUpdate() {
        needsUpdate = true;
    }

    private synchronized void processUpdates() {
        if(backGeometry != null) {
            if(!backGeometry.isLoaded() || !backGeometry.isServerResourceReady()) return;

            Geometry oldGeometry = frontGeometry;
            frontGeometry = backGeometry;
            backGeometry = null;
            if(oldGeometry != null) oldGeometry.unload();
        }

        if(!needsUpdate) return;

        Font font = getFont();
        if(font == null || !font.isLoaded()) return;

        material.setAtlas(font.getAtlas());
        backGeometry = new TextGeometry(getText(), font.getMetadata(), getHAlign(), getVAlign(), getAutoWrap(), getWrapSize(),
                getCharSpacing(), getLineSpacing());

        needsUpdate = false;
    }
}
