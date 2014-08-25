package physics;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

//TODO: Write this class
public class PhysicsThread extends GameThread {

	private PhysicsEngine engine;

	private long setupDelay = 2000;
	private boolean setupStage = true;

	public PhysicsThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);

		engine = new PhysicsEngine(assContainer);
		assContainer.setPhysicsEngine(engine);
	}

	@Override
	protected void gameLoop() {
		if (setupStage) {
			try {
				Thread.sleep(setupDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setupStage = false;
		}
		engine.stepSimulation();
	}

}
