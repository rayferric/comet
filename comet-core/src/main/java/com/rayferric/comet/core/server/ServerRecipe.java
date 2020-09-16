package com.rayferric.comet.core.server;

public abstract class ServerRecipe {
    public ServerRecipe(Runnable cleanUpCallback) {
        this.cleanUpCallback = cleanUpCallback;
    }

    public void cleanUp() {
        if(cleanUpCallback != null) cleanUpCallback.run();
    }

    private final Runnable cleanUpCallback;
}
