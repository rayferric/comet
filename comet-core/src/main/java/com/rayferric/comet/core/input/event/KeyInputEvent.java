package com.rayferric.comet.core.input.event;

import com.rayferric.comet.core.input.InputKey;

public class KeyInputEvent implements InputEvent {
    public enum Type {
        PRESS, ECHO, RELEASE
    }

    public KeyInputEvent(Type type, InputKey key) {
        this.type = type;
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public InputKey getKey() {
        return key;
    }

    private final Type type;
    private final InputKey key;
}
