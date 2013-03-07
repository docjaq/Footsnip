package control;

import org.lwjgl.input.Keyboard;

import thread.GameThread;
import assets.Player;

/**
 * First stab at a thread for handling input for player.
 */
public class ControlThread extends GameThread {

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean upPressed = false;
	private boolean downPressed = false;

	public ControlThread(Player player, int threadDelay) {
		super(player, threadDelay);
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
		// TODO: This really doesn't make any sense, but, meh.
		if (leftPressed || rightPressed) {
			player.accelerateRotation();
			if (leftPressed) {
				player.rotateCCW();
			} else {
				player.rotateCW();
			}
		} else {
			player.resetRotationSpeed();
		}

		player.move();
		if (downPressed || upPressed) {

			// player.accelerateMovement();
			if (downPressed) {
				// player.moveBackward();
				player.delerateMovement();
			} else {
				// player.moveForward();
				player.accelerateMovement();
			}
		} else {
			// player.resetMovementSpeed();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
			player.increaseScale();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
			player.decreaseScale();
		}
	}
}
