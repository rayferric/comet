package com.rayferric.comet.server;

/**
 * Internal resources are API-specific server-side resources
 * created from recipes composed by the base resources.
 * (i.e. parent resource asks the server to create another resource for itself to have control of,
 * in exchange, the server gains permission to reload the base resource on-demand)
 * <p></p>
 * Internal resources are mapped to their corresponding "parent" resources using a hash map,
 * which enables the server to reload them.
 * <p></p>
 * User can manually reload a base resource and recreate its
 * server-side "reflection" (if exists) by invoking Resource.reload().
 */
public interface ServerResource {
    void free();
}
