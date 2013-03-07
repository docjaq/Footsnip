package thread;

import assets.Player;

public abstract class GameThread extends Thread {
	/** Everything must need a player. */
	// TODO: J: Should this be here? If this is a standard game thread? I'm
	// confused.
	protected Player player;

	/** Flag to determine when to stop the loop. */
	private boolean timeToStop = false;

	/** Pause between iterations. */
	private int threadDelay;

	public GameThread(Player player, int threadDelay) {
		this.player = player;
		this.threadDelay = threadDelay;
	}

	public void stopThread() {
		timeToStop = true;
	}

	public void run() {
		while (!timeToStop) {
			gameLoop();

			try {
				Thread.sleep(threadDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void gameLoop();
}
