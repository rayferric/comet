package com.rayferric.comet.core.scenegraph.resource;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.engine.ResourceManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for all resources.<br>
 * • Is fully thread-safe.
 */
public abstract class Resource {
    /**
     * Tells whether the resource is loaded.<br>
     * • May be called from any thread.
     *
     * @return true if loaded
     */
    public boolean isLoaded() {
        return loaded.get();
    }

    /**
     * Tells whether the resource is being currently loaded.<br>
     * • May be called from any thread.
     *
     * @return true if loading
     */
    public boolean isLoading() {
        return loading.get();
    }

    /**
     * Starts loading this resource.<br>
     * • Executes only if the resource is not (being) loaded at the time.<br>
     * • This is mostly a non-blocking routine.<br>
     * • May be called from any thread.
     *
     * @return false if the resource was already (being) loaded
     */
    public boolean load() {
        synchronized(loadingLock) {
            return !loaded.get() && loading.compareAndSet(false, true);
        }
    }

    /**
     * Unregisters this resource from {@link Engine#getResourceManager() resource manager}
     * and starts unloading the server resource.<br>
     * • Executes only if the resource is loaded, use {@link #isLoaded()} to check for that.<br>
     * • May be called from any thread.
     *
     * @return false if the resource was already unloaded
     */
    public boolean unload() {
        synchronized(loadingLock) {
            if(!loaded.compareAndSet(true, false))
                return false;
            Engine.getInstance().getResourceManager().unregisterUnloadedResource(this);
            return true;
        }
    }

    /**
     * If loaded, calls {@link #unload()}, then executes {@link #load()}.<br>
     * • This is a non-blocking routine.<br>
     * • May be called from any thread.
     */
    public void reload() {
        synchronized(loadingLock) {
            if(loaded.get()) unload();
            load();
        }
    }

    /**
     * Marks this resource as loaded and {@link ResourceManager#registerLoadedResource(Resource) submits} it to the resource manager.<br>
     * • May be called from any thread.
     *
     * @throws IllegalStateException if multiple threads managed to be loading the same resource simultaneously (this should not happen and is a bug)
     */
    protected void finishLoading() {
        synchronized(loadingLock) {
            // This is a theoretically unreachable block, there's an internal programming error if it throws:
            if(!loaded.compareAndSet(false, true) || !loading.compareAndSet(true, false))
                throw new IllegalStateException(
                        "A single resource was being loaded using multiple threads simultaneously.");

            Engine.getInstance().getResourceManager().registerLoadedResource(this);
        }
    }

    private final AtomicBoolean loading = new AtomicBoolean(false);
    private final AtomicBoolean loaded = new AtomicBoolean(false);
    protected final Object loadingLock = new Object();
}
