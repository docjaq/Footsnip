package physics;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

//TODO: Write this class
public class PhysicsThread extends GameThread {

	private PhysicsEngine engine;

	public PhysicsThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);

		engine = new PhysicsEngine(assContainer);
	}

	@Override
	protected void gameLoop() {
		engine.stepSimulation();
	}

}
