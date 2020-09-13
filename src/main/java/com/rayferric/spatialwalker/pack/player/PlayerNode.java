package com.rayferric.spatialwalker.pack.player;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.input.InputAxis;
import com.rayferric.comet.input.InputKey;
import com.rayferric.comet.input.InputManager;
import com.rayferric.comet.input.event.InputEvent;
import com.rayferric.comet.input.event.KeyInputEvent;
import com.rayferric.comet.math.*;
import com.rayferric.comet.scenegraph.node.AudioPlayer;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.physics.Area;
import com.rayferric.comet.scenegraph.node.physics.PhysicsBody;
import com.rayferric.comet.scenegraph.node.physics.RayCast;

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

        cameraPitch = getChild("Camera Pitch");
        cameraNode = cameraPitch.getChild("Camera");
        feetArea = (Area)getChild("Feet Area");
        footstepsAudioPlayer = (AudioPlayer)getChild("Footsteps Audio Player");
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
        boolean sprinting = inputManager.getActionState("Sprint");

        float pitchDelta = inputLookPitch * 0.1F;
        float yawDelta = inputLookYaw * 0.1F;

        float pitch = cameraPitch.getTransform().getRotation().toEuler().getX();
        float yaw = getTransform().getRotation().toEuler().getY();

        pitch = Mathf.clamp(pitch + pitchDelta, -89, 89);
        yaw += yawDelta;

        cameraPitch.getTransform().setRotation(pitch, 0, 0);
        getTransform().setRotation(0, yaw, 0);

        Vector3f moveDir = new Vector3f(inputMoveX, 0, inputMoveZ).normalize();
        moveDir = Quaternion.axisAngle(Vector3f.UP, yaw).mul(moveDir);

        Vector3f currentVelocity = getLinearVelocity().mul(new Vector3f(1, 0, 1));
        Vector3f targetVelocity = moveDir.mul(sprinting ? MAX_SPEED * SPRINT_FACTOR : MAX_SPEED);
        Vector3f deltaVelocity = targetVelocity.sub(currentVelocity);
        Vector3f moveForce = deltaVelocity.mul((float)delta * ACCELERATION);

        boolean onFloor = feetArea.getBodies().size() > 1;

        if(onFloor) {
            applyForce(ForceType.ACCELERATION_IMPULSE, moveForce);


            float shakeStrength = this.shakeStrength = Mathf.lerp(this.shakeStrength, targetVelocity.length() > 0.1F ? (sprinting ? SPRINT_FACTOR : 1) : 0, (float)delta * SHAKE_ATTENUATION);

            shakeTime += (float)delta * shakeStrength;
            float shakeX = Mathf.sin(shakeTime * 360 * SHAKE_SPEED) * SHAKE_WIDTH * shakeStrength;
            float shakeY = Mathf.sin(shakeTime * 360 * (SHAKE_SPEED + 1)) * SHAKE_WIDTH * shakeStrength;
            cameraNode.getTransform().setTranslation(shakeX, shakeY, 0);

            if(inputManager.getActionJustPressed("Jump"))
                applyForce(ForceType.ACCELERATION_IMPULSE, Vector3f.UP.mul(6F));
        } else
            applyForce(ForceType.ACCELERATION_IMPULSE, moveForce.mul(AIR_CONTROL));

        footstepsAudioPlayer.setPlaying(onFloor && (inputMoveX != 0 || inputMoveZ != 0));
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

    private static final float MAX_SPEED = 4;
    private static final float SPRINT_FACTOR = 1.5F;
    private static final float ACCELERATION = 4;
    private static final float SHAKE_ATTENUATION = 10;
    private static final float AIR_CONTROL = 0.0625F;
    private static final float SHAKE_SPEED = 1;
    private static final float SHAKE_WIDTH = 0.025F;

    private Node cameraPitch;
    private Node cameraNode;
    private Area feetArea;
    private AudioPlayer footstepsAudioPlayer;

    private boolean focused = false;
    private float shakeTime = 0;
    private float shakeStrength = 0;
}
