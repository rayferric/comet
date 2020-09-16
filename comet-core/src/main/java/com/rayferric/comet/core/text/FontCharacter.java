package com.rayferric.comet.core.text;

import com.rayferric.comet.core.math.Vector2f;

public class FontCharacter {
    public FontCharacter(Vector2f pos, Vector2f size, Vector2f offset, float advance) {
        this.pos = pos;
        this.size = size;
        this.offset = offset;
        this.advance = advance;
    }

    @Override
    public String toString() {
        return String.format("FontCharacter{pos=%s, size=%s, offset=%s, advance=%s}", pos, size, offset, advance);
    }

    public Vector2f getPos() {
        return pos;
    }

    public Vector2f getSize() {
        return size;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public float getAdvance() {
        return advance;
    }

    // Lower left atlas texture coordinate, character size in atlas and offset of
    // the lower left corner in relation to the cursor X position (when Y == 0).
    // The offset and advance properties' unit is total line height.
    private final Vector2f pos, size, offset;
    private final float advance;
}
