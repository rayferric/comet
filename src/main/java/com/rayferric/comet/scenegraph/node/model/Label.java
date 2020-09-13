package com.rayferric.comet.scenegraph.node.model;

import com.rayferric.comet.math.Vector4f;
import com.rayferric.comet.scenegraph.common.Surface;
import com.rayferric.comet.scenegraph.common.material.FontMaterial;
import com.rayferric.comet.scenegraph.resource.font.Font;
import com.rayferric.comet.scenegraph.resource.video.mesh.Mesh;
import com.rayferric.comet.scenegraph.resource.video.mesh.TextMesh;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;
import com.rayferric.comet.util.AtomicFloat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Label extends Model {
    public Label() {
        setName("Label");
        enableUpdate();

        addSurface(new Surface(null, new FontMaterial()));
    }

    public FontMaterial getMaterial() {
        return (FontMaterial)getSurface(0).getMaterial();
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        if(!this.text.getAndSet(text).equals(text))
            requireUpdate();
    }

    public Font getFont() {
        return font.get();
    }

    public void setFont(Font font) {
        this.font.set(font);
        requireUpdate();
    }

    public Vector4f getColor() {
        return getMaterial().getColor();
    }

    public void setColor(Vector4f color) {
        getMaterial().setColor(color);
    }

    public float getCutoff() {
        return getMaterial().getCutoff();
    }

    public void setCutoff(float cutoff) {
        getMaterial().setCutoff(cutoff);
    }

    public float getSoftness() {
        return getMaterial().getSoftness();
    }

    public void setSoftness(float softness) {
        getMaterial().setSoftness(softness);
    }

    public boolean getShowBounds() {
        return getMaterial().getShowBounds();
    }

    public void setShowBounds(boolean show) {
        getMaterial().setShowBounds(show);
    }

    public HorizontalAlignment getHAlign() {
        return hAlign.get();
    }

    public void setHAlign(HorizontalAlignment alignment) {
        hAlign.set(alignment);
        requireUpdate();
    }

    public VerticalAlignment getVAlign() {
        return vAlign.get();
    }

    public void setVAlign(VerticalAlignment alignment) {
        vAlign.set(alignment);
        requireUpdate();
    }

    public boolean getAutoWrap() {
        return autoWrap.get();
    }

    public void setAutoWrap(boolean enabled) {
        autoWrap.set(enabled);
        requireUpdate();
    }

    public float getWrapSize() {
        return wrapSize.get();
    }

    public void setWrapSize(float size) {
        wrapSize.set(size);
        requireUpdate();
    }

    public float getCharSpacing() {
        return charSpacing.get();
    }

    public void setCharSpacing(float spacing) {
        charSpacing.set(spacing);
        requireUpdate();
    }

    public float getLineSpacing() {
        return lineSpacing.get();
    }

    public void setLineSpacing(float spacing) {
        lineSpacing.set(spacing);
        requireUpdate();
    }

    @Override
    protected synchronized void update(double delta) {
        super.update(delta);

        if(nextMesh != null) {
            if(!nextMesh.isLoaded() || !nextMesh.isServerResourceReady()) return;

            Surface surface = getSurface(0);
            Mesh oldMesh = surface.getMesh();
            surface.setMesh(nextMesh);
            nextMesh = null;
            if(oldMesh != null) oldMesh.unload();
        }

        if(!needsUpdate) return;

        Font font = getFont();
        if(font == null || !font.isLoaded()) return;

        getMaterial().setAtlas(font.getAtlas());
        nextMesh = new TextMesh(getText(), font.getMetadata(), getHAlign(), getVAlign(), getAutoWrap(), getWrapSize(),
                getCharSpacing(), getLineSpacing());

        needsUpdate = false;
    }

    private boolean needsUpdate = true;
    private Mesh nextMesh = null;
    private final AtomicReference<String> text = new AtomicReference<>("");
    private final AtomicReference<Font> font = new AtomicReference<>(null);
    private final AtomicReference<HorizontalAlignment> hAlign = new AtomicReference<>(HorizontalAlignment.LEFT);
    private final AtomicReference<VerticalAlignment> vAlign = new AtomicReference<>(VerticalAlignment.TOP);
    private final AtomicBoolean autoWrap = new AtomicBoolean(false);
    private final AtomicFloat wrapSize = new AtomicFloat(0);
    private final AtomicFloat charSpacing = new AtomicFloat(1);
    private final AtomicFloat lineSpacing = new AtomicFloat(1);

    private synchronized void requireUpdate() {
        needsUpdate = true;
    }
}
