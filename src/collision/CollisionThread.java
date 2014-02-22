package collision;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;

public class CollisionThread extends GameThread {

	public CollisionThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	@Override
	protected void gameLoop() {

		TileDataStructure2D tiles = assContainer.getTileDataStructure();
		for (AbstractTile tile : tiles.getTilesAsList()) {

			/*
			 * Create a copy of this list before we send it to the collision
			 * method. If we don't do this, if one of the entities in the list
			 * changes tile, it will A) throw a null pointer exception in the
			 * collision list, and B) not correctly test that entity for
			 * collision!
			 */
			List<Entity> collisionCopyList = new ArrayList<Entity>(tile.getContainedEntities());
			CollisionMethods.checkEntityCollisions(collisionCopyList);
			collisionCopyList = null;
		}
	}
}
