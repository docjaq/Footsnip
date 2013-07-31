package assets.world.datastructures;

import java.util.ArrayList;
import java.util.List;

import assets.world.AbstractTile;

public interface TileDataStructure {

	public void init(AbstractTile tile);

	public List<AbstractTile> getAllTiles();

	public void populateNeighbouringTiles(AbstractTile tile);

	public ArrayList<AbstractTile> getNeighbouringTiles(AbstractTile tile);

	public AbstractTile getTileTop(AbstractTile tile);

	public AbstractTile getTileTopRight(AbstractTile tile);

	public AbstractTile getTileRight(AbstractTile tile);

}
