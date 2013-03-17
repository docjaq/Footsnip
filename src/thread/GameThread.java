package thread;

import main.Main;
import assets.Player;

public abstract class GameThread extends Thread {
	protected Main mainApplication;

	/** Everything must need a player. */
	// TODO: J: Should this be here? If this is a standard game thread? I'm
	// confused.
	protected Player player;

	/**
	 * TODO: Is this a bit shit? Probably - a flag to show that the set up stuff
	 * of this thread is done. Some kind of listener pattern would be better.
	 */
	private boolean setupDone = false;

	/** Flag to determine when to stop the loop. */
	private boolean timeToStop = false;

	/** Pause between iterations. */
	private int threadDelay;

	public GameThread(Player player, int threadDelay, Main mainApplication) {
		this.player = player;
		this.threadDelay = threadDelay;
		this.mainApplication = mainApplication;
	}

	public void stopThread() {
		timeToStop = true;
	}

	public void run() {
		beforeLoop();
		setupDone = true;

		while (!timeToStop) {
			gameLoop();

			try {
				Thread.sleep(threadDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		afterLoop();
	}

	protected void beforeLoop() {
		// No default functionality.
	}

	protected void afterLoop() {
		// No default functionality.
	}

	protected abstract void gameLoop();

	public boolean isSetupDone() {
		return setupDone;
	}
}
