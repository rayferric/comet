package com.rayferric.comet.manager;

import com.rayferric.comet.scenegraph.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps track of loaded resources.<br>
 */
public class ResourceManager {
    /**
     * Reloads all tracked resources.
     * • May be called from any thread.
     *
     * @param type type of resources to reload
     */
    public <T> void reloadResources(Class<T> type) {
        for(Resource resource : snapLoadedResources()) {
            if(type.isAssignableFrom(resource.getClass()))
                resource.reload();
        }
    }

    /**
     * Returns a snapshot of a {@link ArrayList list} of currently loaded {@link Resource resources}.<br>
     * • Is internally used by the servers.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @return a snapshot of the resource registry
     */
    public List<Resource> snapLoadedResources() {
        synchronized(resources) {
            return new ArrayList<Resource>(resources);
        }
    }

    /**
     * Registers a {@link Resource}.<br>
     * • Is internally called by {@link Resource} when it has finished loading.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param resource resource, must not be null
     */
    public void registerLoadedResource(Resource resource) {
        resources.add(resource);
    }

    /**
     * Unregisters a {@link Resource}.<br>
     * • Is internally called by {@link Resource} when it has started unloading.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param resource resource, must not be null
     */
    public void unregisterUnloadedResource(Resource resource) {
        resources.remove(resource);
    }

    private final List<Resource> resources = Collections.synchronizedList(new ArrayList<>());
}
