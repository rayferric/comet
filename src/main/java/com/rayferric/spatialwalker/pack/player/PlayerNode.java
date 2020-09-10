package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.InputAxis;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.input.InputManager;
import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.input.event.KeyInputEvent;
import com.rayferric.comet.math.Mathf;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
import com.rayferric.comet.scenegraph.resource.physics.shape.CapsuleCollisionShape;

public class PlayerNode extends PhysicsBody {
    public PlayerNode() {
        setName("Player");
        enableUpdate();
        enableInput();

        InputManager inputManager = Engine.getInstance().getInputManager();
        inputManager.addTrackAxis("Look Yaw", InputAxis.MOUSE_X, -1);
        inputManager.addTrackAxis("Look Pitch", InputAxis.MOUSE_Y, -1);
    }

    @Override
    protected void init() {
        super.init();

        cameraNode = getChild("Camera");

        addCollider(new Collider(new CapsuleCollisionShape(0.5F, 1), Matrix4f.IDENTITY));
        setMass(70F);
        setAngularFactor(new Vector3f(0, 1, 0));
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        if(!focused) return;

        InputManager inputManager = Engine.getInstance().getInputManager();
        float inputLookYaw = inputManager.getTrackValue("Look Yaw");
        float inputLookPitch = inputManager.getTrackValue("Look Pitch");

        float pitchDelta = inputLookPitch * 0.1F;
        float yawDelta = inputLookYaw * 0.1F;

        float pitch = cameraNode.getTransform().getRotation().toEuler().getX();

        pitch = Mathf.clamp(pitch + pitchDelta, -89, 89);

        cameraNode.getTransform().setRotation(pitch, 0, 0);
        getTransform().rotate(0, yawDelta, 0);
    }

    @Override
    protected void input(InputEvent event) {
        super.input(event);

        KeyInputEvent keyEvent = (event instanceof KeyInputEvent) ? (KeyInputEvent) event : null;
        if(keyEvent == null) return;

        if(keyEvent.getType() != KeyInputEvent.Type.RELEASE)return;

        InputManager inputManager = Engine.getInstance().getInputManager();
        if(keyEvent.getKey() == InputKey.KEYBOARD_ESCAPE) {
            if(inputManager.isMouseCentered()) {
                inputManager.setMouseCentered(false);
                inputManager.setMouseHidden(false);
                focused = false;
            } else {
                inputManager.setMouseCentered(true);
                inputManager.setMouseHidden(true);
                focused = true;
            }
        }

        if(keyEvent.getKey() == InputKey.KEYBOARD_SPACE) {
            applyForce(ForceType.ACCELERATION_IMPULSE, Vector3f.UP.mul(2F));
        }
    }

    private Node cameraNode;

    private boolean focused = false;
}
