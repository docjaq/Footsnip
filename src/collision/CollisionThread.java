package collision;

import java.util.ArrayList;
import java.util.List;

import main.Main;
import thread.GameThread;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.entities.Projectile;

public class CollisionThread extends GameThread {

	public CollisionThread(AssetContainer assContainer, int threadDelay, Main mainApplication) {
		super(assContainer, threadDelay, mainApplication, "Collision Thread");
	}

	@Override
	protected void gameLoop() {
		List<Entity> entities = new ArrayList<Entity>(assContainer.getMonsters().size() + assContainer.getProjectiles().size() + 1);
		entities.add(assContainer.getPlayer());
		entities.addAll(assContainer.getMonsters());

		for (Projectile projectile : assContainer.getProjectiles()) {
			if (projectile.readyForCollisionDetection()) {
				entities.add(projectile);
			}
		}

		CollisionMethods.checkEntityCollisions(entities);
	}
}
