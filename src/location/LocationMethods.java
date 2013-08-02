package location;

import assets.entities.Entity;
import assets.world.datastructures.TileDataStructure;

//TODO: Write this class
public class LocationMethods {

	public static void locateEntity(Entity entity, TileDataStructure data) {

		// TODO: Work out what tile the player is in
		// TODO: Send the containing tile to the player (for now, not entirely
		// sure this is useful yet)
		entity.locatedWithin(data.getTileTop(data.getInitialTile()));
	}
}
