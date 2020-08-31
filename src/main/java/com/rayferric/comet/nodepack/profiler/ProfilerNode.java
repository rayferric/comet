package com.rayferric.comet.nodepack.profiler;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.profiling.Profiler;
import com.rayferric.comet.profiling.TimeAccumulator;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.model.Label;
import com.rayferric.comet.util.Timer;
import com.rayferric.comet.video.util.VideoInfo;

import java.util.Locale;

public class ProfilerNode extends Node {
    @Override
    protected void init() {
        super.init();

        fpsLabel = (Label)getChild("FPS Label");
        frameTimeLabel = (Label)getChild("Frame Time Label");
        cpuTimeLabel = (Label)getChild("CPU Time Label");
        gpuTimeLabel = (Label)getChild("GPU Time Label");
        cheapMemoryLabel = (Label)getChild("Cheap Memory Label");
        videoMemoryLabel = (Label)getChild("Video Memory Label");
        videoApiVersionLabel = (Label)getChild("Video API Version Label");
        shaderVersionLabel = (Label)getChild("Shader Version Label");

        timer.start();
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        Vector2i windowSize = Engine.getInstance().getVideoServer().getWindow().getFramebufferSize();
        float ratio = (float)windowSize.getX() / windowSize.getY();
        Transform t = new Transform(getTransform());
        t.setTranslation(ratio, 1, 0);
        t.setScale((float)PIXEL_SCALE / windowSize.getY());
        setTransform(t);

        if(timer.getElapsed() > UPDATE_DELAY) {
            timer.reset();

            Profiler profiler = Engine.getInstance().getProfiler();
            TimeAccumulator cpuTimer = profiler.getCpuAccumulator();
            TimeAccumulator gpuTimer = profiler.getGpuAccumulator();

            VideoInfo videoInfo = Engine.getInstance().getVideoServer().getVideoInfo();

            String fpsStr = String.format(Locale.US, "%.0f FPS", 1 / cpuTimer.getAvg());
            String frameTimeStr = String.format(Locale.US, "%.2f ms", cpuTimer.getAvg() * 1e+3);
            String cpuTimeStr =
                    String.format(Locale.US, "CPU: %3.0f %3.0f %3.0f", cpuTimer.getMin() * 1e+3,
                            cpuTimer.getAvg() * 1e+3,
                            cpuTimer.getMax() * 1e+3);
            String gpuTimeStr =
                    String.format(Locale.US, "GPU: %3.0f %3.0f %3.0f", gpuTimer.getMin() * 1e+3,
                            gpuTimer.getAvg() * 1e+3,
                            gpuTimer.getMax() * 1e+3);

            long totalCheapMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long usedCheapMem = totalCheapMem - Runtime.getRuntime().freeMemory() / (1024 * 1024);
            String cheapMemoryStr = String.format("RAM: %4d/%4d MiB", usedCheapMem, totalCheapMem);

            long totalVideMem = videoInfo.getTotalVRam() / (1024 * 1024);
            long usedVideoMem = totalVideMem - videoInfo.getFreeVRam() / (1024 * 1024);
            String videoMemoryStr = String.format("VRAM: %4d/%4d MiB", usedVideoMem, totalVideMem);


            fpsLabel.setText(fpsStr);
            frameTimeLabel.setText(frameTimeStr);
            cpuTimeLabel.setText(cpuTimeStr);
            gpuTimeLabel.setText(gpuTimeStr);
            cheapMemoryLabel.setText(cheapMemoryStr);
            videoMemoryLabel.setText(videoMemoryStr);
            videoApiVersionLabel.setText(videoInfo.getApiVersion());
            shaderVersionLabel.setText(videoInfo.getShaderVersion());
        }
    }

    private static final int PIXEL_SCALE = 60;
    private static final double UPDATE_DELAY = 0.1;

    private Label fpsLabel, frameTimeLabel, cpuTimeLabel, gpuTimeLabel, cheapMemoryLabel, videoMemoryLabel,
            videoApiVersionLabel, shaderVersionLabel;
    private Timer timer = new Timer();
}
