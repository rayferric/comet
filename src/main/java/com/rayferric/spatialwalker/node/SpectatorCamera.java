package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.*;
import com.rayferric.comet.math.Mathf;
import com.rayferric.comet.math.Quaternion;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;
import com.rayferric.comet.video.Window;

public class SpectatorCamera extends PerspectiveCamera {
    public SpectatorCamera(float near, float far, float fov) {
        super(near, far, fov);
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

        inputManager.addTrackKey("Move Y", InputKey.KEYBOARD_SPACE, 1);
        inputManager.addTrackKey("Move Y", InputKey.KEYBOARD_SHIFT_LEFT, -1);

        inputManager.addActionKey("Sprint", InputKey.KEYBOARD_CTRL_LEFT);
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        if(!focused) return;

        InputManager inputManager = Engine.getInstance().getInputManager();

        Transform transform = new Transform(getTransform());

        float inputLookYaw = inputManager.getTrackValue("Look Yaw");
        float inputLookPitch = inputManager.getTrackValue("Look Pitch");
        float inputMoveZ = inputManager.getTrackValue("Move Z");
        float inputMoveX = inputManager.getTrackValue("Move X");
        float inputMoveY = inputManager.getTrackValue("Move Y");

        if(smoothing) {
            pitchMomentum = Mathf.lerp(pitchMomentum, 0, (float)delta * MOUSE_DECELERATION);
            yawMomentum = Mathf.lerp(yawMomentum, 0, (float)delta * MOUSE_DECELERATION);

            pitchMomentum += inputLookPitch * MOUSE_ACCELERATION;
            yawMomentum += inputLookYaw * MOUSE_ACCELERATION;

            Vector3f euler = transform.getRotation().toEuler();
            float pitch = Mathf.clamp(euler.getX() + pitchMomentum * (float)delta, -89, 89);
            float yaw = euler.getY() + yawMomentum * (float)delta;
            transform.setRotation(pitch, yaw, 0);

            float maxSpeed = MAX_SPEED, acceleration = ACCELERATION;
            if(inputManager.getActionState("Sprint")) {
                maxSpeed *= 4;
                acceleration *= 4;
            }

            Vector3f moveDir = new Vector3f(inputMoveX, inputMoveY, inputMoveZ).normalize();
            moveDir = Quaternion.eulerAngle(pitch, yaw, 0).mul(moveDir);

            if(moveDir.length() == 0) velocity = velocity.lerp(new Vector3f(0), (float)delta * acceleration);
            velocity = velocity.add(moveDir.mul((float)delta * acceleration));
            velocity = velocity.normalize().mul(Math.min(velocity.length(), maxSpeed));

            transform.translate(velocity.mul((float)delta));
        } else {
            float pitchDelta = inputLookPitch * 0.075F;
            float yawDelta = inputLookYaw * 0.075F;

            Vector3f euler = transform.getRotation().toEuler();
            float pitch = Mathf.clamp(euler.getX() + pitchDelta, -89, 89);
            float yaw = euler.getY() + yawDelta;
            transform.setRotation(pitch, yaw, 0);

            float speed = MAX_SPEED;
            if(inputManager.getActionState("Sprint"))speed *= 4;

            Vector3f moveDir = new Vector3f(inputMoveX, inputMoveY, inputMoveZ).normalize().mul(speed);
            moveDir = Quaternion.eulerAngle(pitch, yaw, 0).mul(moveDir);
            transform.translate(moveDir.mul((float)delta));
        }

        setTransform(transform);
    }

    @Override
    protected void input(InputEvent event) {
        super.input(event);

        if(event.getType() != InputEvent.Type.RELEASE)return;

        InputManager inputManager = Engine.getInstance().getInputManager();
        if(event.getKey() == InputKey.KEYBOARD_ESCAPE) {
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
        if(event.getKey() == InputKey.KEYBOARD_C)
            smoothing = !smoothing;
        if(event.getKey() == InputKey.KEYBOARD_F) {
            Window wnd = Engine.getInstance().getVideoServer().getWindow();
            wnd.setFullscreen(!wnd.isFullscreen());
        }
    }

    private static final float MAX_SPEED = 4;
    private static final float ACCELERATION = 16;
    private static final float MOUSE_ACCELERATION = 2;
    private static final float MOUSE_DECELERATION = 32;

    private boolean focused = false;
    private Vector3f velocity = new Vector3f(0);
    private float pitchMomentum = 0, yawMomentum = 0;
    private boolean smoothing = true;
}
