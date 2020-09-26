package com.rayferric.comet.core.profiling;

public class Profiler {
    public TimeAccumulator getCpuAccumulator() {
        return cpuAccumulator;
    }

    public TimeAccumulator getGpuAccumulator() {
        return gpuAccumulator;
    }

    private final TimeAccumulator cpuAccumulator = new TimeAccumulator(10);
    private final TimeAccumulator gpuAccumulator = new TimeAccumulator(10);
}
