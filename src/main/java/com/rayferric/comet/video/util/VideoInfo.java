package com.rayferric.comet.video.util;

import java.util.concurrent.atomic.AtomicInteger;

public class VideoInfo {
    public VideoInfo(String deviceVendor, String deviceModel, String apiVersion, String shaderVersion, int totalVRam) {
        this.deviceVendor = deviceVendor;
        this.deviceModel = deviceModel;
        this.apiVersion = apiVersion;
        this.shaderVersion = shaderVersion;
        this.totalVRam = totalVRam;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getShaderVersion() {
        return shaderVersion;
    }

    public int getTotalVRam() {
        return totalVRam;
    }

    public int getFreeVRam() {
        return freeVRam.get();
    }

    public void setFreeVRam(int freeVRam) {
        this.freeVRam.set(freeVRam);
    }

    private final String deviceVendor, deviceModel, apiVersion, shaderVersion;
    private final int totalVRam;
    private final AtomicInteger freeVRam = new AtomicInteger(0);
}
