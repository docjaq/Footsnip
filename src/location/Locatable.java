package location;

import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure;

public interface Locatable {
	public void locatedWithin(AbstractTile tile, TileDataStructure data);
}
