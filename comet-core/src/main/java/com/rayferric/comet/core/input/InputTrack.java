package com.rayferric.comet.core.input;

import java.util.EnumMap;
import java.util.Map;

public class InputTrack {
    public float getValue(InputManager manager) {
        float value = 0;
        for(Map.Entry<InputKey, Float> entry : keys.entrySet())
            value += (manager.getKeyState(entry.getKey()) ? 1 : 0) * entry.getValue();
        for(Map.Entry<InputAxis, Float> entry : axes.entrySet())
            value += manager.getAxisValue(entry.getKey()) * entry.getValue();
        return value;
    }

    public void addKey(InputKey key, float scale) {
        keys.put(key, scale);
    }

    public boolean removeKey(InputKey key) {
        return keys.remove(key) != null;
    }

    public void addAxis(InputAxis axis, float scale) {
        axes.put(axis, scale);
    }

    public boolean removeAxis(InputAxis axis) {
        return axes.remove(axis) != null;
    }

    private final Map<InputKey, Float> keys = new EnumMap<>(InputKey.class);
    private final Map<InputAxis, Float> axes = new EnumMap<>(InputAxis.class);
}
