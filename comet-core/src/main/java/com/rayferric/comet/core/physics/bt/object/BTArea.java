package com.rayferric.comet.core.physics.bt.object;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.util.ObjectArrayList;
import com.rayferric.comet.core.scenegraph.node.physics.Area;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;

import java.util.ArrayList;
import java.util.List;

public class BTArea extends BTObject {
    public BTArea(Object owner) {
        btObj = new GhostObject();
        btObj.setCollisionShape(new CompoundShape());
        btObj.setCollisionFlags(btObj.getCollisionFlags() | CollisionFlags.NO_CONTACT_RESPONSE);
        btObj.setUserPointer(owner);
    }

    public GhostObject getBtArea() {
        return (GhostObject)btObj;
    }

    public List<PhysicsBody> getBodies() {
        GhostObject btArea = getBtArea();
        int numBodies = btArea.getNumOverlappingObjects();

        List<PhysicsBody> bodies = new ArrayList<>(numBodies);
        for(int i = 0; i < numBodies; i++) {
            CollisionObject btColObj = btArea.getOverlappingObject(i);
            if(btColObj instanceof RigidBody && hasContact(btColObj))
                bodies.add((PhysicsBody)btColObj.getUserPointer());
        }
        return bodies;
    }

    public List<Area> getAreas() {
        GhostObject btArea = getBtArea();
        int numBodies = btArea.getNumOverlappingObjects();

        List<Area> areas = new ArrayList<>(numBodies);
        for(int i = 0; i < numBodies; i++) {
            CollisionObject btColObj = btArea.getOverlappingObject(i);
            if(btColObj instanceof GhostObject && hasContact(btColObj))
                areas.add((Area)btColObj.getUserPointer());
        }
        return areas;
    }

    private boolean hasContact(CollisionObject btObj) {
        BroadphaseProxy objProxy = btObj.getBroadphaseHandle();
        BroadphasePair pair = world.getBtWorld().getPairCache().findPair(objProxy, this.btObj.getBroadphaseHandle());
        if(pair == null) return false;

        ObjectArrayList<PersistentManifold> manifolds = new ObjectArrayList<>();
        pair.algorithm.getAllContactManifolds(manifolds);

        boolean contact = false;
        for(PersistentManifold manifold : manifolds)
            if(manifold.getNumContacts() > 0) contact = true;
        return contact;
    }
}
