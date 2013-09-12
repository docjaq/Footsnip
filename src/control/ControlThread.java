package control;

import java.util.concurrent.CyclicBarrier;

import main.Main;

import org.lwjgl.input.Keyboard;

import thread.GameThread;
import util.Utils;
import assets.AssetContainer;

/**
 * First stab at a thread for handling input for player.
 */
public class ControlThread extends GameThread {

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean upPressed = false;
	private boolean downPressed = false;

	/** The time of the last iteration, to calculate the time delta. */
	private long lastIterationTime;

	public ControlThread(AssetContainer assContainer, Main mainApplication, CyclicBarrier barrier) {
		super(assContainer, mainApplication, barrier);

		// Initialise the delta.
		getIterationDelta();
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

		int timeDelta = getIterationDelta();

		// All the time that either left or right are pressed, increase the
		// rotation speed.
		if (leftPressed || rightPressed) {
			assContainer.getPlayer().accelerateRotation();
			if (leftPressed) {
				assContainer.getPlayer().rotateCCW(timeDelta);
			} else {
				assContainer.getPlayer().rotateCW(timeDelta);
			}
		} else {
			assContainer.getPlayer().resetRotationSpeed();
		}

		assContainer.getPlayer().move(timeDelta);

		if (downPressed || upPressed) {
			if (downPressed) {
				assContainer.getPlayer().decelerateMovement();
			} else {
				assContainer.getPlayer().accelerateMovement();
			}
		}
	}

	/**
	 * Calculate the time delta between now and the previous iteration.
	 * 
	 * @return Milliseconds since the last iteration.
	 */
	private int getIterationDelta() {
		long time = Utils.getTime();
		int delta = (int) (time - lastIterationTime);
		lastIterationTime = time;

		return delta;
	}
}
