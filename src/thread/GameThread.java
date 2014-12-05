package thread;

import java.util.ArrayList;
import java.util.List;

import main.GameControl;
import main.GameState;
import main.Main;
import assets.AssetContainer;

public abstract class GameThread implements Runnable, ObservableThread {
	protected Main mainApplication;

	protected AssetContainer assContainer;

	/** List of listeners that care about when the initial setup is complete. */
	private List<ThreadObserver> setupObservers = new ArrayList<ThreadObserver>();

	/** Pause between iterations. */
	private int threadDelay;

	public GameThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		this.assContainer = assContainer;
		this.threadDelay = threadDelay;
		this.mainApplication = mainApplication;
	}

	public void run() {
		setup();

		try {
			while (GameControl.getGameState() == GameState.PLAYING) {
				gameLoop();
				Thread.sleep(threadDelay);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			afterLoop();
		}
	}

	protected void beforeLoop() {
		// No default functionality.
		System.out.println("beforeLoop: " + this.toString());
	}

	protected void afterLoop() {
		// No default functionality.
		System.out.println("afterLoop: " + this.toString());
	}

	protected abstract void gameLoop();

	protected void setup() {
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
