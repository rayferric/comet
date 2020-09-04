package com.rayferric.comet.physics.bt;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.server.ServerResource;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class BTRigidBody implements ServerResource {
    public BTRigidBody(DiscreteDynamicsWorld world) {
        mass = 1;

        Transform bodyTransform = new Transform();
        bodyTransform.setIdentity();
        DefaultMotionState motionState = new DefaultMotionState(bodyTransform);

        CollisionShape boxShape = new BoxShape(new Vector3f(1, 1, 1));
        Transform boxTransform = new Transform();
        boxTransform.setIdentity();
        collisionShape.addChildShape(boxTransform, boxShape);
        collisionShape.calculateLocalInertia(mass, inertia);

        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(
                mass,
                motionState,
                collisionShape,
                inertia
        );
        rigidBody = new RigidBody(info);

        (this.world = world).addRigidBody(rigidBody);
    }

    @Override
    public void destroy() {
        rigidBody.destroy();
    }

    public Matrix4f getTransform() {
        Transform btTransform = new Transform();
        btTransform.setIdentity();
        rigidBody.getMotionState().getWorldTransform(btTransform);
        float[] glMatrix = new float[16];
        btTransform.getOpenGLMatrix(glMatrix);
        return new Matrix4f(
                glMatrix[0], glMatrix[1], glMatrix[2], glMatrix[3],
                glMatrix[4], glMatrix[5], glMatrix[6], glMatrix[7],
                glMatrix[8], glMatrix[9], glMatrix[10], glMatrix[11],
                glMatrix[12], glMatrix[13], glMatrix[14], glMatrix[15]
        );
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    private final DiscreteDynamicsWorld world;
    private final RigidBody rigidBody;
    private final CompoundShape collisionShape = new CompoundShape();
    private float mass;
    private final Vector3f inertia = new Vector3f();
}
