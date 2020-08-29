package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.util.Timer;
import org.lwjgl.system.CallbackI;

public class Rotor extends Node {
    // Thread that instantiated the scene (or any other)
    public Rotor() {
        setName("Rotor");
        timer.start();
        Engine.getInstance().getProfiler().setTimerSamples(100);
    }

    // Thread that instantiated the scene (or any other)
    // Called as soon as node hierarchy of the scene is fully assembled
    @Override
    protected void init() {

    }

    // Main thread
    @Override
    protected void update(double delta) {
        Transform transform = new Transform(getTransform());
        transform.rotate(0, (float)(45 * delta), 0);
        setTransform(transform);

        if(timer.getElapsed() > 1) {
            timer.reset();
            double videoCpuTime = Engine.getInstance().getProfiler().getVideoCpuTime();
            double videoGpuTime = Engine.getInstance().getProfiler().getVideoGpuTime();
            System.out.printf("Video CPU time: %s (%s FPS)\n", videoCpuTime, Math.round(1 / videoCpuTime));
            System.out.printf("Video GPU time: %s (%s FPS)\n", videoGpuTime, Math.round(1 / videoGpuTime));

        }
    }

    // Planned: fixedUpdate(double) input(InputEvent) mainUpdate(double)

    private final Timer timer = new Timer();
}
