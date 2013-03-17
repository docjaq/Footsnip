package thread;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import assets.Player;

public abstract class GameThread extends Thread implements ObservableThread {
	protected Main mainApplication;

	/** Everything must need a player. */
	// TODO: J: Should this be here? If this is a standard game thread? I'm
	// confused.
	protected Player player;

	/** List of listeners that care about when the initial setup is complete. */
	private List<ThreadObserver> setupObservers = new ArrayList<ThreadObserver>();

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
		setup();

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

	private void setup() {
		beforeLoop();

		// Notify observers that setup is complete.
		for (ThreadObserver observer : setupObservers) {
			observer.setupDone(this);
		}
	}

	public void registerSetupObserver(ThreadObserver observer) {
		setupObservers.add(observer);
	}
}
