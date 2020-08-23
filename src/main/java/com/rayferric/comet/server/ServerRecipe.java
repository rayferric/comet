package com.rayferric.comet.server;

public abstract class ServerRecipe {
    public ServerRecipe(Runnable cleanUpCallback) {
        this.cleanUpCallback = cleanUpCallback;
    }

    public Runnable getCleanUpCallback() {
        return cleanUpCallback;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        this.handle = handle;
    }

    private final Runnable cleanUpCallback;
    private long handle = 0;
}
