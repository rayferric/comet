package com.rayferric.comet.core.nodepack.profiler;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.input.event.InputEvent;
import com.rayferric.comet.core.input.InputKey;
import com.rayferric.comet.core.input.event.KeyInputEvent;
import com.rayferric.comet.core.math.Mathf;
import com.rayferric.comet.core.math.Transform;
import com.rayferric.comet.core.math.Vector2i;
import com.rayferric.comet.core.profiling.Profiler;
import com.rayferric.comet.core.profiling.TimeAccumulator;
import com.rayferric.comet.core.scenegraph.node.Node;
import com.rayferric.comet.core.scenegraph.node.model.Graph;
import com.rayferric.comet.core.scenegraph.node.model.Label;
import com.rayferric.comet.core.util.Timer;
import com.rayferric.comet.core.video.util.VideoInfo;

import java.util.*;

public class ProfilerNode extends Node {
    public ProfilerNode() {
        setName("Profiler");
        enableUpdate();
        enableInput();
    }

    @Override
    protected void init() {
        super.init();

        fpsLabel = (Label)getChild("FPS Label");
        frameTimeLabel = (Label)getChild("Frame Time Label");
        cpuTimeLabel = (Label)getChild("CPU Time Label");
        gpuTimeLabel = (Label)getChild("GPU Time Label");
        heapMemoryLabel = (Label)getChild("Heap Memory Label");
        videoMemoryLabel = (Label)getChild("Video Memory Label");
        verticesLabel = (Label)getChild("Vertices Label");
        trianglesLabel = (Label)getChild("Triangles Label");
        videoApiVersionLabel = (Label)getChild("Video API Version Label");
        shaderVersionLabel = (Label)getChild("Shader Version Label");
        cpuGraph = (Graph)getChild("CPU Graph");
        gpuGraph = (Graph)getChild("GPU Graph");

        timer.start();
        Arrays.fill(cpuGraphValues, 0);
        Arrays.fill(gpuGraphValues, 0);
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        Vector2i windowSize = Engine.getInstance().getVideoServer().getWindow().getFramebufferSize();
        float ratio = (float)windowSize.getX() / windowSize.getY();
        Transform t = getTransform();
        t.setTranslation(ratio, 1, 0);
        t.setScale((float)PIXEL_SCALE / windowSize.getY());

        if(timer.getElapsed() > UPDATE_DELAY) {
            timer.reset();

            Profiler profiler = Engine.getInstance().getProfiler();
            TimeAccumulator frameTimer = profiler.getFrameAccumulator();
            TimeAccumulator cpuTimer = profiler.getCpuAccumulator();
            TimeAccumulator gpuTimer = profiler.getGpuAccumulator();

            System.arraycopy(cpuGraphValues, 1, cpuGraphValues, 0, cpuGraphValues.length - 1);
            cpuGraphValues[cpuGraphValues.length - 1] = Mathf.clamp((float)cpuTimer.getAvg() / CPU_GRAPH_RANGE, 0.01F, 1);

            System.arraycopy(gpuGraphValues, 1, gpuGraphValues, 0, gpuGraphValues.length - 1);
            gpuGraphValues[gpuGraphValues.length - 1] = Mathf.clamp((float)gpuTimer.getAvg() / GPU_GRAPH_RANGE, 0.01F, 1);

            if(!isVisible()) return;

            String fpsStr = String.format(Locale.US, "%.0f FPS", 1 / frameTimer.getAvg());
            String frameTimeStr = String.format(Locale.US, "%.2f ms", frameTimer.getAvg() * 1e+3);
            String cpuTimeStr =
                    String.format(Locale.US, "CPU: %3.0f %3.0f %3.0f", cpuTimer.getMin() * 1e+3,
                            cpuTimer.getAvg() * 1e+3,
                            cpuTimer.getMax() * 1e+3);
            String gpuTimeStr =
                    String.format(Locale.US, "GPU: %3.0f %3.0f %3.0f", gpuTimer.getMin() * 1e+3,
                            gpuTimer.getAvg() * 1e+3,
                            gpuTimer.getMax() * 1e+3);

            long totalHeapMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long usedHeapMem = totalHeapMem - Runtime.getRuntime().freeMemory() / (1024 * 1024);
            String heapMemoryStr = String.format("Heap: %4d/%4d MiB", usedHeapMem, totalHeapMem);

            VideoInfo videoInfo = Engine.getInstance().getVideoServer().getVideoInfo();

            long totalVideMem = videoInfo.getTotalVRam() / (1024 * 1024); // B => MiB
            long usedVideoMem = totalVideMem - videoInfo.getFreeVRam() / (1024 * 1024); // B => MiB
            String videoMemoryStr = String.format("VRAM: %4d/%4d MiB", usedVideoMem, totalVideMem);

            String verticesStr = String.format("Vertices: %6d", videoInfo.getVertexCount());
            String trianglesStr = String.format("Triangles: %6d", videoInfo.getTriangleCount());

            fpsLabel.setText(fpsStr);
            frameTimeLabel.setText(frameTimeStr);
            cpuTimeLabel.setText(cpuTimeStr);
            gpuTimeLabel.setText(gpuTimeStr);
            heapMemoryLabel.setText(heapMemoryStr);
            videoMemoryLabel.setText(videoMemoryStr);
            verticesLabel.setText(verticesStr);
            trianglesLabel.setText(trianglesStr);
            videoApiVersionLabel.setText(videoInfo.getApiVersion());
            shaderVersionLabel.setText(videoInfo.getShaderVersion());

            cpuGraph.setValues(cpuGraphValues);
            gpuGraph.setValues(gpuGraphValues);
        }
    }

    @Override
    protected void input(InputEvent event) {
        super.input(event);

        KeyInputEvent keyEvent = (event instanceof KeyInputEvent) ? (KeyInputEvent) event : null;
        if(keyEvent == null) return;

        if(keyEvent.getType() != KeyInputEvent.Type.RELEASE)return;

        if(keyEvent.getKey() == InputKey.KEYBOARD_F1)
            setVisible(!isVisible());
    }

    private static final int PIXEL_SCALE = 60;
    private static final double UPDATE_DELAY = 0.1;
    private static final float CPU_GRAPH_RANGE = 0.016667F * 4;
    private static final float GPU_GRAPH_RANGE = 0.016667F * 4;

    private Label fpsLabel, frameTimeLabel, cpuTimeLabel, gpuTimeLabel, heapMemoryLabel, videoMemoryLabel, verticesLabel, trianglesLabel, videoApiVersionLabel, shaderVersionLabel;
    private Graph cpuGraph, gpuGraph;

    private final Timer timer = new Timer();
    private final float[] cpuGraphValues = new float[256];
    private final float[] gpuGraphValues = new float[256];
}
