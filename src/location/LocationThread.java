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
		// LocationMethods.locateEntities();
		System.out.println("Locating");
	}
}
