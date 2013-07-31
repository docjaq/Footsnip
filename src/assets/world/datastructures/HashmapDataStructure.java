package assets.world.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import assets.world.AbstractTile;
import assets.world.TileFactory;

public class HashmapDataStructure implements TileDataStructure {

	private HashMap<HashmapKey, AbstractTile> map;
	private List<AbstractTile> list; // Backed by map
	private static final HashmapKey INITIAL_KEY = new HashmapKey(0, 0);

	public HashmapDataStructure(AbstractTile tile) {
		map = new HashMap<HashmapKey, AbstractTile>();
		list = new ArrayList<AbstractTile>(map.values());
		map.put(INITIAL_KEY, tile);
	}

	public HashmapDataStructure() {
		map = new HashMap<HashmapKey, AbstractTile>();
	}

	public void init(AbstractTile tile) {
		map.put(INITIAL_KEY, tile);
	}

	public List<AbstractTile> getAllTiles() {
		return list;
	}

	@Override
	public ArrayList<AbstractTile> getNeighbouringTiles(AbstractTile tile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTile getTileTop(AbstractTile tile) {
		return map.get(new HashmapKey(tile.getKey().x, tile.getKey().y + 1));
	}

	@Override
	public AbstractTile getTileTopRight(AbstractTile tile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTile getTileRight(AbstractTile tile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateNeighbouringTiles(AbstractTile tile) {
		addTile(tile.getKey(), tile);

	}

	private void addTile(HashmapKey key, AbstractTile tile) {
		map.put(key, TileFactory.createTile(tile, new Vector3f(0, tile.getSize(), 0)));
	}
}
