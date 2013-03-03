package control;

import org.lwjgl.input.Keyboard;

import assets.Player;

/**
 * First stab at a thread for handling input for player. TODO: Should probably
 * extend some abstract base thread.
 */
public class ControlThread extends Thread {
	private Player player;

	private boolean stop = false;

	public ControlThread(Player player) {
		this.player = player;
	}

	public void run() {
		// Allows you to hold the key down
		// Keyboard.enableRepeatEvents(true);

		boolean leftPressed = false;
		boolean rightPressed = false;
		boolean upPressed = false;
		boolean downPressed = false;

		while (!stop) {
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

			// Ditto for up/down.
			if (downPressed || upPressed) {
				player.accelerateMovement();
				if (downPressed) {
					player.moveBackward();
				} else {
					player.moveForward();
				}
			} else {
				player.resetMovementSpeed();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
				player.increaseScale();
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
				player.decreaseScale();
			}

			// TODO: There must be a better option than this...
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void kill() {
		stop = true;
	}
}
