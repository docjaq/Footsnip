package location;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import assets.entities.Entity;
import assets.entities.Monster;
import assets.world.AbstractTile;
import assets.world.datastructures.DataStructureKey2D;
import assets.world.datastructures.TileDataStructure;

public class LocationMethods {

	private static final float HALF_TILE_WIDTH = AbstractTile.SIZE / 2f;

	private static AbstractTile locateEntity(Entity entity, TileDataStructure data) {

		/**
		 * Tiles are actually offset by 0.5: initial tile is [-0.5,0.5]^2, so we
		 * must adjust the containment check
		 **/
		Vector3f modelPos = entity.getModel().modelPos;
		float xPos = modelPos.x;
		float yPos = modelPos.y;

		if (entity instanceof Monster) {
			System.out.println(modelPos.x + "," + modelPos.y);
		}

		if (xPos > 0) {
			xPos += HALF_TILE_WIDTH;
		} else {
			xPos -= HALF_TILE_WIDTH;
		}
		if (yPos > 0) {
			yPos += HALF_TILE_WIDTH;
		} else {
			yPos -= HALF_TILE_WIDTH;
		}
		DataStructureKey2D key = new DataStructureKey2D((int) (xPos / AbstractTile.SIZE), (int) (yPos / AbstractTile.SIZE));

		return data.getTileUsingKey(key);
	}

	public static void locatePlayer(Entity entity, TileDataStructure data) {
		entity.locatedWithin(locateEntity(entity, data), data);
	}

	// TODO: Had to change this to List<Monsters> from List<Entity> because it
	// said the type was wrong. This confused me entirely as Monster subclasses
	// Entity, but I changed it for now.
	public static void locateMonsters(List<Monster> monsters, TileDataStructure data) {
		for (Monster m : monsters) {
			m.locatedWithin(locateEntity(m, data), data);
		}
	}
}
