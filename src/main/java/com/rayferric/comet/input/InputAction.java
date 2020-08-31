package com.rayferric.comet.input;

import java.util.EnumSet;
import java.util.Set;

public class InputAction {
    public boolean getState(InputManager manager) {
        boolean state = false;
        for(InputKey key : keys)
            state = state || manager.getKeyState(key);
        return state;
    }

    public boolean getJustPressed(InputManager manager) {
        boolean state = false;
        for(InputKey key : keys)
            state = state || manager.getKeyJustPressed(key);
        return state;
    }

    public boolean getJustReleased(InputManager manager) {
        boolean state = false;
        for(InputKey key : keys)
            state = state || manager.getKeyJustReleased(key);
        return state;
    }

    public void addKey(InputKey key) {
        keys.add(key);
    }

    public boolean removeKey(InputKey key) {
        return keys.remove(key);
    }

    private final Set<InputKey> keys = EnumSet.noneOf(InputKey.class);
}
