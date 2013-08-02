package assets.world.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import renderer.GLWorld;
import assets.world.AbstractTile;
import assets.world.TileFactory;

public class HashmapDataStructure implements TileDataStructure {

	private HashMap<HashmapKey, AbstractTile> map;
	private List<AbstractTile> list; // Backed by map
	private static final HashmapKey INITIAL_KEY = new HashmapKey(0, 0);
	private AbstractTile initialTile;

	public HashmapDataStructure() {
		map = new HashMap<HashmapKey, AbstractTile>();
	}

	public void init(AbstractTile initialTile) {
		this.initialTile = initialTile;
		initialTile.setKey(INITIAL_KEY);
		map.put(INITIAL_KEY, initialTile);
	}

	public List<AbstractTile> getTilesAsList() {
		return new ArrayList<AbstractTile>(map.values());
	}

	@Override
	public HashMap<HashmapKey, AbstractTile> getTilesAsHashMap() {
		return map;
	}

	@Override
	public void draw(GLWorld glWorld) {
		glWorld.copyCameraMatricesToShader(initialTile.getModel().getShader());
		for (AbstractTile t : map.values()) {
			t.getModel().draw();
		}

	}

	@Override
	public void populateNeighbouringTiles(AbstractTile tile) {

		HashmapKey parentKey = tile.getKey();
		addTile(parentKey, 0, 1, tile);
		addTile(parentKey, 1, 1, tile);
		addTile(parentKey, 1, 0, tile);
		addTile(parentKey, 1, -1, tile);
		addTile(parentKey, 0, -1, tile);
		addTile(parentKey, -1, -1, tile);
		addTile(parentKey, -1, 0, tile);
		addTile(parentKey, -1, +1, tile);
	}

	private void addTile(HashmapKey parentKey, int xAdjust, int yAdjust, AbstractTile tile) {

		int adjustedX = parentKey.x + xAdjust;
		int adjustedY = parentKey.y + yAdjust;
		Vector3f position = new Vector3f(adjustedX * tile.getSize(), adjustedY * tile.getSize(), 0);
		HashmapKey key = new HashmapKey(adjustedX, adjustedY);

		if (map.containsKey(key)) {
			System.out.println("Key (" + key.x + "," + key.y + ") already exists!");
		} else {
			map.put(key, TileFactory.createTile(key, tile, tile.getClass(), position));
		}
	}

	// @Override
	// public ArrayList<AbstractTile> getNeighbouringTiles(AbstractTile tile) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// @Override
	public AbstractTile getTileTop(AbstractTile tile) {
		return map.get(new HashmapKey(tile.getKey().x, tile.getKey().y + 1));
	}

	@Override
	public AbstractTile getTileTopRight(AbstractTile tile) {
		return map.get(new HashmapKey(tile.getKey().x + 1, tile.getKey().y + 1));
	}

	@Override
	public AbstractTile getTileRight(AbstractTile tile) {
		return map.get(new HashmapKey(tile.getKey().x + 1, tile.getKey().y));
	}

	@Override
	public AbstractTile getInitialTile() {
		return initialTile;
	}
}
