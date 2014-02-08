package thread;

import main.Main;
import assets.AssetContainer;

/**
 * Super simple specialist GameThread, which just overrides the run method, to
 * skip the sleep functionality, for the renderer, which handles that itself
 * through LWJGLs Display class.
 * 
 * @author dave
 */
public abstract class RendererThread extends GameThread {

	public RendererThread(AssetContainer assContainer, Main mainApplication) {
		super(assContainer, -1, mainApplication);
	}

	@Override
	public void run() {
		setup();

		while (!timeToStop) {
			gameLoop();
		}

		afterLoop();
	}
}
