package com.rayferric.comet.core.server;

import com.rayferric.comet.core.scenegraph.resource.Resource;

/**
 * • Server resources are API-specific server-side resources
 * created from recipes composed by the base resources.<br>
 * <p>
 * • Server resources are mapped to their corresponding "parent" resources using a hash map,
 * which enables the server to decode base resources supplied by nodes and components.<br>
 * <p>
 * • User can manually reload a base resource and recreate its
 * server-side resource by invoking {@link Resource#reload()}.
 */
public interface ServerResource {
    void destroy();
}
