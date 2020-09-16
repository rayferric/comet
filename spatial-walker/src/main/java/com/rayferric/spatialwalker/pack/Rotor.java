package com.rayferric.spatialwalker.pack;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.input.event.InputEvent;
import com.rayferric.comet.core.input.InputKey;
import com.rayferric.comet.core.input.event.KeyInputEvent;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.core.video.api.VideoAPI;

public class Rotor extends Node {
    // Thread that instantiated the scene (or any other)
    public Rotor() {
        setName("Rotor");
        enableUpdate();
        enableInput();
    }

    // Main thread
    @Override
    protected void update(double delta) {
        super.update(delta);

        getTransform().rotate(0, (float)(45 * delta), 0);
    }

    @Override
    protected void input(InputEvent event) {
        super.input(event);

        KeyInputEvent keyEvent = (event instanceof KeyInputEvent) ? (KeyInputEvent) event : null;
        if(keyEvent == null) return;

        if(keyEvent.getType() != KeyInputEvent.Type.RELEASE) return;

        InputKey key = keyEvent.getKey();

        if(key == InputKey.KEYBOARD_R)
            Engine.getInstance().getVideoServer().setApi(VideoAPI.OPENGL);

        if(key == InputKey.KEYBOARD_F)
            Engine.getInstance().getVideoServer().getWindow().setFullscreen(
                    !Engine.getInstance().getVideoServer().getWindow().isFullscreen());

        if(key == InputKey.KEYBOARD_T)
            Engine.getInstance().getResourceManager().reloadResources(VideoResource.class);
    }

    // Planned: fixedUpdate(double) input(InputEvent) mainUpdate(double)
}
