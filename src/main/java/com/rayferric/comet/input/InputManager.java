package com.rayferric.comet.input;

import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.input.event.KeyInputEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputManager {
    // <editor-fold desc="State Queries">

    /**
     * Must only be called from the main thread.
     */
    public List<InputEvent> getEvents() {
        return new ArrayList<>(events);
    }

    /**
     * Must only be called from the main thread.
     */
    public boolean getKeyState(InputKey key) {
        Boolean state = keyStates.get(key);
        return state != null && state;
    }

    /**
     * Must only be called from the main thread.
     */
    public boolean getKeyJustPressed(InputKey key) {
        return justPressedKeys.contains(key);
    }

    /**
     * Must only be called from the main thread.
     */
    public boolean getKeyJustReleased(InputKey key) {
        return justReleasedKeys.contains(key);
    }

    /**
     * Must only be called from the main thread.
     */
    public float getAxisValue(InputAxis axis) {
        Float value = axisValues.get(axis);
        return value != null ? value : 0;
    }

    // </editor-fold>

    // <editor-fold desc="State Management">

    /**
     * Must only be called from the main thread.
     */
    public void resetState() {
        events.clear();
        justPressedKeys.clear();
        justReleasedKeys.clear();

        setAxisValue(InputAxis.MOUSE_X, 0);
        setAxisValue(InputAxis.MOUSE_Y, 0);
        setAxisValue(InputAxis.MOUSE_WHEEL_Y, 0);
    }

    /**
     * Intended to populate input state after resetting the manager.<br>
     * Must only be called from the main thread.
     */
    public void enqueueEvent(InputEvent event) {
        events.add(event);
    }

    /**
     * Intended to populate input state after resetting the manager.<br>
     * Must only be called from the main thread.
     */
    public void setAxisValue(InputAxis axis, float value) {
        axisValues.put(axis, value);
    }

    /**
     * Intended to populate input state further after enqueueing the events.<br>
     * Must only be called from the main thread.
     */
    public void processEvents() {
        for(InputEvent event : events) {
            KeyInputEvent keyEvent = (event instanceof KeyInputEvent) ? (KeyInputEvent) event : null;
            if(keyEvent == null) continue;

            KeyInputEvent.Type type = keyEvent.getType();
            InputKey key = keyEvent.getKey();

            if(type == KeyInputEvent.Type.PRESS) {
                keyStates.put(key, true);
                justPressedKeys.add(keyEvent.getKey());
            } else if(type == KeyInputEvent.Type.RELEASE) {
                keyStates.put(key, false);
                justReleasedKeys.add(keyEvent.getKey());
            }
        }
    }

    // </editor-fold>

    // <editor-fold desc="Action and Track Queries">

    /**
     * Must only be called from the main thread.
     */
    public boolean getActionState(String name) {
        synchronized(actions) {
            InputAction action = actions.get(name);
            return action != null && action.getState(this);
        }
    }

    /**
     * Must only be called from the main thread.
     */
    public boolean getActionJustPressed(String name) {
        synchronized(actions) {
            InputAction action = actions.get(name);
            return action != null && action.getJustPressed(this);
        }
    }

    /**
     * Must only be called from the main thread.
     */
    public boolean getActionJustReleased(String name) {
        synchronized(actions) {
            InputAction action = actions.get(name);
            return action != null && action.getJustReleased(this);
        }
    }

    /**
     * Must only be called from the main thread.
     */
    public float getTrackValue(String name) {
        synchronized(tracks) {
            InputTrack track = tracks.get(name);
            return track != null ? track.getValue(this) : 0;
        }
    }

    // </editor-fold>

    // <editor-fold desc="Action and Track Management">

    /**
     * May be called from any thread.
     */
    public void addActionKey(String name, InputKey key) {
        synchronized(actions) {
            InputAction action = actions.get(name);
            if(action == null)
                actions.put(name, action = new InputAction());
            action.addKey(key);
        }
    }

    /**
     * May be called from any thread.
     */
    public boolean removeActionKey(String name, InputKey key) {
        synchronized(actions) {
            InputAction action = actions.get(name);
            if(action != null)
                return action.removeKey(key);
            return false;
        }
    }

    /**
     * May be called from any thread.
     */
    public void addTrackKey(String name, InputKey key, float scale) {
        synchronized(tracks) {
            InputTrack track = tracks.get(name);
            if(track == null)
                tracks.put(name, track = new InputTrack());
            track.addKey(key, scale);
        }
    }

    /**
     * May be called from any thread.
     */
    public boolean removeTrackKey(String name, InputKey key) {
        synchronized(tracks) {
            InputTrack track = tracks.get(name);
            if(track != null)
                return track.removeKey(key);
            return false;
        }
    }

    /**
     * May be called from any thread.
     */
    public void addTrackAxis(String name, InputAxis axis, float scale) {
        synchronized(tracks) {
            InputTrack track = tracks.get(name);
            if(track == null)
                tracks.put(name, track = new InputTrack());
            track.addAxis(axis, scale);
        }
    }

    /**
     * May be called from any thread.
     */
    public boolean removeTrackAxis(String name, InputAxis axis) {
        synchronized(tracks) {
            InputTrack track = tracks.get(name);
            if(track != null)
                return track.removeAxis(axis);
            return false;
        }
    }

    // </editor-fold>

    // <editor-fold desc="Mouse Control">

    /**
     * May be called from any thread.
     */
    public boolean isMouseCentered() {
        return mouseCentered.get();
    }

    /**
     * May be called from any thread.
     */
    public void setMouseCentered(boolean centered) {
        mouseCentered.set(centered);
    }

    /**
     * May be called from any thread.
     */
    public boolean isMouseHidden() {
        return mouseHidden.get();
    }

    /**
     * May be called from any thread.
     */
    public void setMouseHidden(boolean hidden) {
        mouseHidden.set(hidden);
    }


    // </editor-fold>

    private final List<InputEvent> events = new ArrayList<>();

    private final Map<InputKey, Boolean> keyStates = new EnumMap<>(InputKey.class);
    private final Set<InputKey> justPressedKeys = EnumSet.noneOf(InputKey.class);
    private final Set<InputKey> justReleasedKeys = EnumSet.noneOf(InputKey.class);
    private final Map<InputAxis, Float> axisValues = new EnumMap<>(InputAxis.class);

    private final Map<String, InputAction> actions = new HashMap<>();
    private final Map<String, InputTrack> tracks = new HashMap<>();

    private final AtomicBoolean mouseCentered = new AtomicBoolean(false);
    private final AtomicBoolean mouseHidden = new AtomicBoolean(false);
}
