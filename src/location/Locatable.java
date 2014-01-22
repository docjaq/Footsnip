package location;

import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;

public interface Locatable {
	public void locatedWithin(AbstractTile tile, TileDataStructure2D data);
}
