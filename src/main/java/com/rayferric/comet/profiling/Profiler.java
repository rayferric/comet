package com.rayferric.comet.profiling;

public class Profiler {
    public TimeAccumulator getCpuAccumulator() {
        return cpuAccumulator;
    }

    public TimeAccumulator getGpuAccumulator() {
        return gpuAccumulator;
    }

    private final TimeAccumulator cpuAccumulator = new TimeAccumulator(5);
    private final TimeAccumulator gpuAccumulator = new TimeAccumulator(5);
}
