package com.rayferric.spatialwalker.node;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.*;
import com.rayferric.comet.math.Quaternion;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.scenegraph.node.camera.PerspectiveCamera;

public class SpectatorCamera extends PerspectiveCamera {
    public SpectatorCamera(float near, float far, float fov) {
        super(near, far, fov);
        enableUpdate();
        enableInput();

        InputManager inputManager = Engine.getInstance().getInputManager();
        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_W, -1);
        inputManager.addTrackKey("Move Z", InputKey.KEYBOARD_S, 1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_A, -1);
        inputManager.addTrackKey("Move X", InputKey.KEYBOARD_D, 1);
        inputManager.addTrackKey("Move Y", InputKey.KEYBOARD_SPACE, 1);
        inputManager.addTrackKey("Move Y", InputKey.KEYBOARD_SHIFT_LEFT, -1);
    }

    @Override
    protected void update(double delta) {
        super.update(delta);

        if(!focused) return;

        InputManager inputManager = Engine.getInstance().getInputManager();

        Transform transform = new Transform(getTransform());

        float inputLookYaw = inputManager.getAxisValue(InputAxis.MOUSE_X);
        float inputLookPitch = inputManager.getAxisValue(InputAxis.MOUSE_Y);
        Vector3f euler = transform.getRotation().toEuler();
        float pitch = euler.getX() - (inputLookPitch * 0.1F);
        float yaw = euler.getY() - (inputLookYaw * 0.1F);
        transform.setRotation(pitch, yaw, 0);

        float inputMoveZ = inputManager.getTrackValue("Move Z");
        float inputMoveX = inputManager.getTrackValue("Move X");
        float inputMoveY = inputManager.getTrackValue("Move Y");
        Vector3f moveDir = new Vector3f(inputMoveX, inputMoveY, inputMoveZ).normalize().mul(FLY_SPEED);
        moveDir = Quaternion.fromEuler(0, yaw, 0).mul(moveDir);
        transform.translate(moveDir.mul((float)delta));

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
    }

    private static final float FLY_SPEED = 4;

    private boolean focused = false;
}
