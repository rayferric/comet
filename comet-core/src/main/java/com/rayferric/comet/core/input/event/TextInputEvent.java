package com.rayferric.comet.core.input.event;

public class TextInputEvent implements InputEvent {
    public TextInputEvent(String text) {
        this.text = text;
    }

    public TextInputEvent(char c) {
        this.text = Character.toString(c);
    }

    public TextInputEvent(int codePoint) {
        text = Character.toString(codePoint);
    }

    public String getText() {
        return text;
    }

    private final String text;
}
