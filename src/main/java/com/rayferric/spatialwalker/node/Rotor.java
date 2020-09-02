package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.InputAxis;
import com.rayferric.comet.input.InputEvent;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.math.*;
import com.rayferric.comet.profiling.TimeAccumulator;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.resource.video.VideoResource;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.api.VideoAPI;
import com.rayferric.comet.video.util.VideoInfo;

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

        InputKey key = event.getKey();

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
