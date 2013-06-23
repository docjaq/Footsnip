package collision;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

public class CollisionThread extends GameThread {

	public CollisionThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	@Override
	protected void gameLoop() {
		CollisionMethods.checkEntityCollisions(assContainer.getEntities());
	}
}
