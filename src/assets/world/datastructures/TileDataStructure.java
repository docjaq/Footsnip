package assets.world.datastructures;

import java.util.HashMap;
import java.util.List;

import renderer.GLWorld;
import assets.world.AbstractTile;

public interface TileDataStructure {

	public void init(AbstractTile tile);

	public List<AbstractTile> getTilesAsList();

	public HashMap<HashmapKey, AbstractTile> getTilesAsHashMap();

	public void populateNeighbouringTiles(AbstractTile tile);

	public void draw(GLWorld glWorld);

	public AbstractTile getInitialTile();

	// TODO: Decide if this is necessary. I can't really remember
	// public ArrayList<AbstractTile> getNeighbouringTiles(AbstractTile tile);

	public AbstractTile getTileTop(AbstractTile tile);

	public AbstractTile getTileTopRight(AbstractTile tile);

	public AbstractTile getTileRight(AbstractTile tile);

}
