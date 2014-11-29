package location;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;

//TODO: Write this class
public class LocationThread extends GameThread {

	public LocationThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	@Override
	protected void gameLoop() {
		/** Doing it as a single entity now to keep it simple **/
		LocationMethods.locatePlayer(assContainer.getPlayer(), assContainer.getTileDataStructure());
		LocationMethods.locateNonPlayers(assContainer.getNonPlayers(), assContainer.getTileDataStructure());
		LocationMethods.locateProjectiles(assContainer.getProjectiles(), assContainer.getTileDataStructure());
	}
}
