package com.rayferric.comet.core.physics;

import com.rayferric.comet.core.engine.Engine;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.scenegraph.node.physics.Area;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.core.scenegraph.resource.physics.PhysicsResource;
import com.rayferric.comet.core.server.ServerResource;

public abstract class PhysicsEngine {
    public void destroy() {
        onStop();
    }

    public void step() {
        onStep();
    }

    // <editor-fold desc="Internal API">

    public abstract ServerResource createPhysicsWorld();

    public abstract ServerResource createBoxCollisionShape(Vector3f size);

    public abstract ServerResource createCapsuleCollisionShape(float radius, float height);

    public abstract ServerResource createCylinderCollisionShape(float radius, float height);

    public abstract ServerResource createSphereCollisionShape(float radius);

    public abstract ServerResource createPhysicsBody(PhysicsBody owner);

    public abstract ServerResource createArea(Area owner);

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

    protected ServerResource getServerResourceOrNull(PhysicsResource resource) {
        if(resource == null) return null;
        long handle = resource.getServerHandle();
        return Engine.getInstance().getPhysicsServer().getServerResource(handle);
    }

    // </editor-fold>
}
