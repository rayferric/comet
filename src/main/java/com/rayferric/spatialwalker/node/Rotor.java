package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.*;
import com.rayferric.comet.profiling.TimeAccumulator;
import com.rayferric.comet.scenegraph.node.Label;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.util.Timer;
import org.lwjgl.system.CallbackI;

public class Rotor extends Node {
    // Thread that instantiated the scene (or any other)
    public Rotor() {
        setName("Rotor");
        timer.start();
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

        if(timer.getElapsed() > 0.0) {
            timer.reset();
            TimeAccumulator cpu = Engine.getInstance().getProfiler().getCpuAccumulator();
            TimeAccumulator gpu = Engine.getInstance().getProfiler().getGpuAccumulator();

            double cpuMin = cpu.getMin() * 1e+3;
            double cpuAvg = cpu.getAvg() * 1e+3;
            double cpuMax = cpu.getMax() * 1e+3;

            double gpuMin = gpu.getMin() * 1e+3;
            double gpuAvg = gpu.getAvg() * 1e+3;
            double gpuMax = gpu.getMax() * 1e+3;

            String cpuStr = String.format("CPU: %.2f ms (%.2f FPS) | %.2f ms - %.2f ms", cpuAvg, 1e+3 / cpuAvg, cpuMin, cpuMax);
            String gpuStr = String.format("GPU: %.2f ms (%.2f FPS) | %.2f ms - %.2f ms", gpuAvg, 1e+3 / gpuAvg, gpuMin, gpuMax);

            Label label = (Label)getChild("Label");
            label.setText(cpuStr);
        }
    }

    // Planned: fixedUpdate(double) input(InputEvent) mainUpdate(double)

    private final Timer timer = new Timer();
}
