package com.rayferric.comet.physics;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.resource.physics.RigidBodyResource;
import com.rayferric.comet.server.ServerResource;

public abstract class PhysicsEngine {
    public void destroy() {
        onStop();
    }

    public void step() {
        onStep();
    }

    // <editor-fold desc="Internal API">

    public abstract ServerResource createBoxCollisionShape(Vector3f extents);

    public abstract ServerResource createSphereCollisionShape(float radius);

    public abstract ServerResource createRigidBody();

    // </editor-fold>

    protected PhysicsEngine() {
        onStart();
    }

    // <editor-fold desc="Events">

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onStep();

    // </editor-fold>

    // <editor-fold desc="Creating and Querying Default Resources"

    protected ServerResource getRigidBodyOrNull(RigidBodyResource rigidBodyResource) {
        if(rigidBodyResource == null || !rigidBodyResource.isLoaded()) return null;
        long handle = rigidBodyResource.getServerHandle();
        return Engine.getInstance().getPhysicsServer().getServerResource(handle);
    }

    // </editor-fold>
}
