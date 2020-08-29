package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.*;
import com.rayferric.comet.profiling.TimeAccumulator;
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
        //setTransform(transform);

        if(timer.getElapsed() > 10) {
            timer.reset();
            TimeAccumulator cpu = Engine.getInstance().getProfiler().getCpuAccumulator();
            TimeAccumulator gpu = Engine.getInstance().getProfiler().getGpuAccumulator();

            double cpuMin = cpu.getMin() * 1e+3;
            double cpuAvg = cpu.getAvg() * 1e+3;
            double cpuMax = cpu.getMax() * 1e+3;

            double gpuMin = gpu.getMin() * 1e+3;
            double gpuAvg = gpu.getAvg() * 1e+3;
            double gpuMax = gpu.getMax() * 1e+3;

            System.out.printf("CPU: %.2f ms (%.2f FPS) - Min: %.2f ms - Max: %.2f ms\n", cpuAvg, 1e+3 / cpuAvg, cpuMin, cpuMax);
            System.out.printf("GPU: %.2f ms (%.2f FPS) - Min: %.2f ms - Max: %.2f ms\n", gpuAvg, 1e+3 / gpuAvg, gpuMin, gpuMax);

        }
    }

    // Planned: fixedUpdate(double) input(InputEvent) mainUpdate(double)

    private final Timer timer = new Timer();
}
