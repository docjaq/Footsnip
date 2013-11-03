package control;

import main.Main;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import thread.GameThread;
import util.Utils;
import assets.AssetContainer;
import assets.entities.Monster;
import assets.entities.Projectile;

/**
 * First stab at a thread for handling input for player.
 */
public class ControlThread extends GameThread {

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean upPressed = false;
	private boolean downPressed = false;
	private boolean firePressed = false;

	/** The time of the last iteration, to calculate the time delta. */
	private long lastIterationTime;

	public ControlThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication, "Control Thread");

		// Initialise the delta.
		getIterationDelta();
	}

	// TODO: Should we be doing something with the getEventNanoseconds method?
	// I'm confused about how this works given that we're taking events off a
	// buffer - what if, since the last iteration, I've pressed left for 1
	// millisecond and right for 5 milliseconds? I should end up turning
	// slightly right, but I think I'll actually just continue forwards.
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

			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE && Keyboard.getEventKeyState()) {
				fireProjectile();
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

		if (downPressed || upPressed) {
			if (downPressed) {
				assContainer.getPlayer().decelerateMovement();
			} else {
				assContainer.getPlayer().accelerateMovement();
			}
		}

		moveEntities(timeDelta);
	}

	private void fireProjectile() {
		System.out.println("Fire");

		Vector3f position = new Vector3f(assContainer.getPlayer().getModel().modelPos);
		Vector3f angle = new Vector3f(assContainer.getPlayer().getModel().modelAngle);
		Vector3f movementVector = new Vector3f(assContainer.getPlayer().getMovementVector());
		float scale = 1.0f;
		assContainer.addProjectile(new Projectile(position, angle, scale, movementVector));
	}

	private void moveEntities(int timeDelta) {
		assContainer.getPlayer().move(timeDelta);

		// int frameDelta = getFrameTimeDelta();
		for (Monster m : assContainer.getMonsters()) {
			m.rotate(timeDelta);
			m.moveRandom();
		}

		for (Projectile projectile : assContainer.getProjectiles()) {
			projectile.move(timeDelta);
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
