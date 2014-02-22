package collision;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.entities.Projectile;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;

public class CollisionThread extends GameThread {

	public CollisionThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication);
	}

	// @Override
	protected void gameLoopOld() {
		// Put the player and monsters in a list
		List<Entity> entities = new ArrayList<Entity>(assContainer.getMonsters().size() + assContainer.getProjectiles().size() + 1);
		entities.add(assContainer.getPlayer());
		entities.addAll(assContainer.getMonsters());

		// If a projectile is ready, add it to the list
		for (Projectile projectile : assContainer.getProjectiles()) {
			if (projectile.readyForCollisionDetection()) {
				entities.add(projectile);
			}
		}

		// System.out.println("Computing " + entities.size() * entities.size() +
		// " collisions");

		// Pass list to pair-wise collision method
		CollisionMethods.checkEntityCollisions(entities);
	}

	@Override
	protected void gameLoop() {

		TileDataStructure2D tiles = assContainer.getTileDataStructure();

		int count = 0;

		for (AbstractTile tile : tiles.getTilesAsList()) {

			CollisionMethods.checkEntityCollisions(tile.getContainedEntities());
			count += tile.getContainedEntities().size() * tile.getContainedEntities().size() - tile.getContainedEntities().size() / 2;
		}

		// System.out.println("Computing " + count + " collisions");
	}
}
