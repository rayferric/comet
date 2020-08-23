package com.rayferric.comet.scenegraph.resource;

import com.rayferric.comet.Engine;
import com.rayferric.comet.manager.ResourceManager;

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
     * • This is a non-blocking routine.<br>
     * • May be called from any thread.
     */
    public void load() {
        if(loaded.get())
            throw new IllegalStateException("Attempted to load an already loaded resource.");
        if(!loading.compareAndSet(false, true))
            throw new IllegalStateException("Attempted to load a resource that is already being loaded.");
    }

    /**
     * Unregisters this resource from {@link Engine#getResourceManager() resource manager} and starts unloading the server resource.<br>
     * • This is a non-blocking routine.<br>
     * • May be called from any thread.
     */
    public void unload() {
        if(!loaded.compareAndSet(true, false))
            throw new IllegalStateException("Attempted to unload an already unloaded resource.");
        Engine.getInstance().getResourceManager().unregisterUnloadedResource(this);
    }

    /**
     * If loaded, calls {@link #unload()}, then executes {@link #load()}.<br>
     * • This is a non-blocking routine.<br>
     * • May be called from any thread.
     */
    public void reload() {
        if(loaded.get()) unload();
        load();
    }

    protected final AtomicBoolean loading = new AtomicBoolean(false);
    protected final AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * Marks this resource as loaded and {@link ResourceManager#registerLoadedResource(Resource) submits} it to the resource manager.<br>
     * • May be called from any thread.
     */
    protected void finishLoading() {
        // This is a theoretically unreachable block, there's an internal programming error if it throws:
        if(!loaded.compareAndSet(false, true) || !loading.compareAndSet(true, false))
            throw new IllegalStateException(
                    "A single resource was being loaded using multiple threads simultaneously.");
        Engine.getInstance().getResourceManager().registerLoadedResource(this);
    }
}
