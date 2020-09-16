package com.rayferric.comet.core.scenegraph.node.physics;

import com.rayferric.comet.core.scenegraph.resource.physics.AreaResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Area extends PhysicsObject {
    public Area() {
        setName("Area");
        resource = new AreaResource(this);
    }

    // <editor-fold desc="Internal API">

    public void internalSetBodies(List<PhysicsBody> bodies) {
        this.bodies.set(bodies);
    }

    public void internalSetAreas(List<Area> areas) {
        this.areas.set(areas);
    }

    // </editor-fold>

    public List<PhysicsBody> getBodies() {
        return bodies.get();
    }

    public List<Area> getAreas() {
        return areas.get();
    }

    private final AtomicReference<List<PhysicsBody>> bodies = new AtomicReference<>(new ArrayList<>(0));
    private final AtomicReference<List<Area>> areas = new AtomicReference<>(new ArrayList<>(0));
}
