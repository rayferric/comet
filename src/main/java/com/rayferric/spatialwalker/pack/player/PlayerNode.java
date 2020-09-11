package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.InputAxis;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.input.InputManager;
import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.input.event.KeyInputEvent;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.common.Collider;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.PhysicsBody;
import com.rayferric.comet.scenegraph.node.RayCast;
import com.rayferric.comet.scenegraph.resource.physics.shape.CapsuleCollisionShape;

public class PlayerNode extends PhysicsBody {
    public PlayerNode() {
        setName("Player");
        enableUpdate();
        enableInput();

        InputManager inputManager = Engine.getInstance().getInputManager();

        inputManager.addTrackAxis("Look Yaw", InputAxis.MOUSE_X, -1);
        inputManager.addTrackAxis("Look Pitch", InputAxis.MOUSE_Y, -1);

        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_W, -1);
        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_S, 1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_A, -1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_D, 1);

        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_UP, -1);
        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_DOWN, 1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_LEFT, -1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_RIGHT, 1);

        inputManager.addActionKey("Jump", InputKey.KEYBOARD_SPACE);
        inputManager.addActionKey("Sprint", InputKey.KEYBOARD_SHIFT_LEFT);
    }

    @Override
    protected void init() {
        super.init();

        cameraNode = getChild("Camera");
        feetRay = (RayCast)getChild("Feet Ray");
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        if(!focused) return;

        InputManager inputManager = Engine.getInstance().getInputManager();
        float inputLookYaw = inputManager.getTrackValue("Look Yaw");
        float inputLookPitch = inputManager.getTrackValue("Look Pitch");
        float inputMoveZ = inputManager.getTrackValue("Move Z");
        float inputMoveX = inputManager.getTrackValue("Move X");

        float pitchDelta = inputLookPitch * 0.1F;
        float yawDelta = inputLookYaw * 0.1F;

        float pitch = cameraNode.getTransform().getRotation().toEuler().getX();
        float yaw = getTransform().getRotation().toEuler().getY();

        pitch = Mathf.clamp(pitch + pitchDelta, -89, 89);
        yaw += yawDelta;

        cameraNode.getTransform().setRotation(pitch, 0, 0);
        getTransform().setRotation(0, yaw, 0);

        Vector3f moveDir = new Vector3f(inputMoveX, 0, inputMoveZ).normalize();
        moveDir = Quaternion.axisAngle(Vector3f.UP, yaw).mul(moveDir);

        applyForce(ForceType.ACCELERATION_IMPULSE, moveDir.mul((float)delta * 10));

        setGravityDisabled(feetRay.getCollisionBody() != null);

        if(inputManager.getActionJustPressed("Jump"))
            applyForce(ForceType.ACCELERATION_IMPULSE, Vector3f.UP.mul(6F));
    }

    @Override
    protected void input(InputEvent event) {
        super.input(event);

        KeyInputEvent keyEvent = (event instanceof KeyInputEvent) ? (KeyInputEvent) event : null;
        if(keyEvent == null) return;
        InputKey key = keyEvent.getKey();
        KeyInputEvent.Type type = keyEvent.getType();

        InputManager inputManager = Engine.getInstance().getInputManager();
        if(key == InputKey.KEYBOARD_ESCAPE && type == KeyInputEvent.Type.RELEASE) {
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
    }

    private Node cameraNode;
    private RayCast feetRay;

    private boolean focused = false;
}
