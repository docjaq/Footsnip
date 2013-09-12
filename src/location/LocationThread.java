package location;

import java.util.concurrent.CyclicBarrier;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

//TODO: Write this class
public class LocationThread extends GameThread {

	public LocationThread(AssetContainer assContainer, Main mainApplication, CyclicBarrier barrier) {
		super(assContainer, mainApplication, barrier);
	}

	@Override
	protected void gameLoop() {
		/** Doing it as a single entity now to keep it simple **/
		LocationMethods.locatePlayer(assContainer.getPlayer(), assContainer.getTileDataStructure());
	}
}
