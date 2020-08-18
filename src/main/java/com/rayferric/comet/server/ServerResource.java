package com.rayferric.comet.server;

/**
 * Server resources are API-specific server-side resources
 * created from recipes composed by the base resources.
 * <p></p>
 * Server resources are mapped to their corresponding "parent" resources using a hash map,
 * which enables the server to reload them on-demand.
 * <p></p>
 * User can manually reload a base resource and recreate its
 * server-side resource by invoking Resource.reload().
 */
public interface ServerResource {
    void free();
}
