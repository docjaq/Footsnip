package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import main.Main;
import assets.AssetContainer;

public abstract class GameThread extends Thread implements ObservableThread {
	protected Main mainApplication;

	/** Everything must need to know about the assets. */
	protected AssetContainer assContainer;

	/** List of listeners that care about when the initial setup is complete. */
	private List<ThreadObserver> setupObservers = new ArrayList<ThreadObserver>();

	/** Flag to determine when to stop the loop. */
	protected boolean timeToStop = false;

	/**
	 * A CyclicBarrier which pauses each thread at the end of an iteration,
	 * until the other threads are ready for the next iteration.
	 */
	private CyclicBarrier barrier;

	public GameThread(AssetContainer assContainer, Main mainApplication, CyclicBarrier barrier) {
		this.assContainer = assContainer;
		this.mainApplication = mainApplication;
		this.barrier = barrier;
	}

	public void stopThread() {
		timeToStop = true;
	}

	public void run() {
		setup();

		while (!timeToStop) {
			gameLoop();

			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
				afterLoop();
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
