package thread;

import main.GameControl;
import main.GameState;
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

	// Override this method so the renderer always runs as fast as possible (now
	// wait());
	@Override
	public void run() {
		setup();

		while (GameControl.getGameState() == GameState.PLAYING) {
			gameLoop();
		}

		afterLoop();
	}
}
