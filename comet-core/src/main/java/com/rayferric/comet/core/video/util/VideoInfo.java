package com.rayferric.comet.core.video.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class VideoInfo {
    public String getDeviceVendor() {
        return deviceVendor.get();
    }

    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor.set(deviceVendor);
    }

    public String getDeviceModel() {
        return deviceModel.get();
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel.set(deviceModel);
    }

    public String getApiVersion() {
        return apiVersion.get();
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion.set(apiVersion);
    }

    public String getShaderVersion() {
        return shaderVersion.get();
    }

    public void setShaderVersion(String shaderVersion) {
        this.shaderVersion.set(shaderVersion);
    }

    public long getTotalVRam() {
        return totalVRam.get();
    }

    public void setTotalVRam(long totalVRam) {
        this.totalVRam.set(totalVRam);
    }

    public long getFreeVRam() {
        return freeVRam.get();
    }

    public void setFreeVRam(long freeVRam) {
        this.freeVRam.set(freeVRam);
    }

    public int getVertexCount() {
        return vertexCount.get();
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount.set(vertexCount);
    }

    public int getTriangleCount() {
        return triangleCount.get();
    }

    public void setTriangleCount(int triangleCount) {
        this.triangleCount.set(triangleCount);
    }

    private final AtomicReference<String>  deviceVendor = new AtomicReference<>("NO GPU VENDOR");
    private final AtomicReference<String> deviceModel = new AtomicReference<>("NO GPU MODEL");
    private final AtomicReference<String> apiVersion = new AtomicReference<>("NO VIDEO API");
    private final AtomicReference<String> shaderVersion = new AtomicReference<>("NO SHADERS");
    private final AtomicLong totalVRam = new AtomicLong(-1);
    private final AtomicLong freeVRam = new AtomicLong(-1);
    private final AtomicInteger vertexCount = new AtomicInteger(-1);
    private final AtomicInteger triangleCount = new AtomicInteger(-1);
}
