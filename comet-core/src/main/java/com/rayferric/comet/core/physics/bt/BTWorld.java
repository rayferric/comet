package com.rayferric.comet.core.physics.bt;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.rayferric.comet.core.math.Vector3f;
import com.rayferric.comet.core.physics.bt.object.BTBody;
import com.rayferric.comet.core.physics.bt.object.BTObject;
import com.rayferric.comet.core.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.core.scenegraph.node.physics.RayCast;
import com.rayferric.comet.core.server.ServerResource;

import java.util.ArrayList;
import java.util.List;

public class BTWorld implements ServerResource {
    public BTWorld() {
        DefaultCollisionConfiguration collisionCfg = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionCfg) {
            // Hides static-static collision debug warning.
            // The code has been 1:1 copied from the source.
            @Override
            public boolean needsCollision(CollisionObject body0, CollisionObject body1) {
                boolean needsCollision = true;
                if((!body0.isActive()) && (!body1.isActive())) needsCollision = false;
                else if(!body0.checkCollideWith(body1)) needsCollision = false;
                return needsCollision;
            }
        };
        DbvtBroadphase broadPhase = new DbvtBroadphase();
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionCfg);
        world.setGravity(new javax.vecmath.Vector3f(0, 0, 0));

        world.getBroadphase().getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
    }

    @Override
    public void destroy() {
        for(BTObject object : new ArrayList<>(objects))
            object.setWorld(null);
        world.destroy();
    }

    // Only to be used by BTPhysicsBody.setWorld(...).
    public void addObject(BTObject obj) {
        objects.add(obj);
        CollisionObject btObj = obj.getBtObj();

        // RigidBody must be added with separate method to set up gravity.
        if(obj instanceof BTBody)
            world.addRigidBody((RigidBody)btObj, obj.getLayer(), obj.getMask());
        else // instanceof BTArea
            world.addCollisionObject(btObj, obj.getLayer(), obj.getMask());
    }

    // Only to be used by BTPhysicsBody.setWorld(...).
    public void removeObject(BTObject obj) {
        if(objects.remove(obj))
            world.removeCollisionObject(obj.getBtObj());
    }

    public Vector3f getGravity() {
        javax.vecmath.Vector3f gravity = new javax.vecmath.Vector3f();
        world.getGravity(gravity);
        return new Vector3f(gravity.x, gravity.y, gravity.z);
    }

    public void setGravity(Vector3f gravity) {
        world.setGravity(new javax.vecmath.Vector3f(gravity.getX(), gravity.getY(), gravity.getZ()));
    }

    public void step(float delta) {
        world.stepSimulation(delta, 8);
    }

    public void processRayCast(RayCast rayCast) {
        if(!rayCast.isActive()) return;

        Vector3f from = rayCast.getGlobalTransform().getTranslation();
        Vector3f to = from.add(rayCast.getVector());
        javax.vecmath.Vector3f btFrom = BTPhysicsEngine.toBtVector(from);
        javax.vecmath.Vector3f btTo = BTPhysicsEngine.toBtVector(to);

        CollisionWorld.ClosestRayResultCallback cb = new CollisionWorld.ClosestRayResultCallback(btFrom, btTo) {
            @Override
            public float addSingleResult(CollisionWorld.LocalRayResult rayResult, boolean normalInWorldSpace) {
                if(rayCast.getIgnoreParent() && rayResult.collisionObject.getUserPointer() == rayCast.getParent())
                    return this.closestHitFraction;
                else
                    return super.addSingleResult(rayResult, normalInWorldSpace);
            }
        };
        cb.collisionFilterGroup = rayCast.getLayer();
        cb.collisionFilterMask = rayCast.getMask();

        world.rayTest(btFrom, btTo, cb);

        if(cb.hasHit()) {
            rayCast.internalSetBody((PhysicsBody)cb.collisionObject.getUserPointer());
            rayCast.internalSetNormal(BTPhysicsEngine.fromBtVector(cb.hitNormalWorld));
        } else {
            rayCast.internalSetBody(null);
            rayCast.internalSetNormal(null);
        }
    }

    public DiscreteDynamicsWorld getBtWorld() {
        return world;
    }

    private final DiscreteDynamicsWorld world;
    private final List<BTObject> objects = new ArrayList<>();
}
