package collision;

import java.util.concurrent.CyclicBarrier;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

public class CollisionThread extends GameThread {

	public CollisionThread(AssetContainer assContainer, Main mainApplication, CyclicBarrier barrier) {
		super(assContainer, mainApplication, barrier);
	}

	@Override
	protected void gameLoop() {
		CollisionMethods.checkEntityCollisions(assContainer.getEntities());
	}
}
