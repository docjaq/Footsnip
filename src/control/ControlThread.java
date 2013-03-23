package control;

import main.Main;

import org.lwjgl.input.Keyboard;

import thread.GameThread;
import assets.AssetContainer;

/**
 * First stab at a thread for handling input for player.
 */
public class ControlThread extends GameThread {

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean upPressed = false;
	private boolean downPressed = false;

	public ControlThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	public void gameLoop() {
		// Iterate over the key events buffer.
		while (Keyboard.next()) {
			// Identify which button(s) have been pressed / released.
			if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
				leftPressed = Keyboard.getEventKeyState();
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
				rightPressed = Keyboard.getEventKeyState();
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
				upPressed = Keyboard.getEventKeyState();
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
				downPressed = Keyboard.getEventKeyState();
			}
		}

		// All the time that either left or right are pressed, increase the
		// rotation speed.
		if (leftPressed || rightPressed) {
			assContainer.getPlayer().accelerateRotation();
			if (leftPressed) {
				assContainer.getPlayer().rotateCCW(assContainer.getFrameDelta());
			} else {
				assContainer.getPlayer().rotateCW(assContainer.getFrameDelta());
			}
		} else {
			assContainer.getPlayer().resetRotationSpeed();
		}

		assContainer.getPlayer().move(assContainer.getFrameDelta());

		if (downPressed || upPressed) {
			if (downPressed) {
				assContainer.getPlayer().decelerateMovement();
			} else {
				assContainer.getPlayer().accelerateMovement();
			}
		}

	}
}
