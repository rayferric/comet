package com.rayferric.comet.input;

public class InputEvent {
    public enum Type {
        PRESS, ECHO, RELEASE
    }

    public InputEvent(Type type, InputKey key) {
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
