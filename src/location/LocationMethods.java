package location;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import assets.entities.Entity;
import assets.world.AbstractTile;
import assets.world.datastructures.DataStructureKey2D;
import assets.world.datastructures.TileDataStructure;

public class LocationMethods {

	private static AbstractTile locateEntity(Entity entity, TileDataStructure data) {

		/**
		 * Tiles are actually offset by 0.5: initial tile is [-0.5,0.5]^2, so we
		 * must adjust the containment check
		 **/
		Vector3f modelPos = entity.getModel().modelPos;
		float xPos = modelPos.x;
		float yPos = modelPos.y;
		if (xPos > 0) {
			xPos += 0.5;
		} else {
			xPos -= 0.5;
		}
		if (yPos > 0) {
			yPos += 0.5;
		} else {
			yPos -= 0.5;
		}
		DataStructureKey2D key = new DataStructureKey2D((int) xPos, (int) yPos);

		return data.getTileUsingKey(key);
	}

	public static void locatePlayer(Entity entity, TileDataStructure data) {
		entity.locatedWithin(locateEntity(entity, data), data);
	}

	public static void locateMonsters(ArrayList<Entity> monsters, TileDataStructure data) {
		// TODO: For loop to locate monsters
	}
}
