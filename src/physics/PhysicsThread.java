package physics;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

//TODO: Write this class
public class PhysicsThread extends GameThread {

	public PhysicsThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	@Override
	protected void gameLoop() {
		System.out.println("Phyiscs thread");
	}
}
