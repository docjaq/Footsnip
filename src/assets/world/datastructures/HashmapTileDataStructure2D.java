package assets.world.datastructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import math.types.MatrixStack;
import math.types.Vector3;
import renderer.GLPosition;
import renderer.glmodels.factories.GLTileFactory;
import renderer.glshaders.GLShader;
import assets.entities.Player;
import assets.world.AbstractTile;
import assets.world.PolygonHeightmapTile;
import camera.CameraModel.ObjectPole;

public class HashmapTileDataStructure2D implements TileDataStructure2D {

	/*
	 * Needs to be Concurrent to allow the draw() and
	 * populateNeighbouringTiles() to play nicely. Ultimately, rendering should
	 * probably take precedence, but this seemed like a decent solution for now
	 */
	private ConcurrentHashMap<DataStructureKey2D, AbstractTile> map;
	// private List<AbstractTile> list; // Backed by map
	private static final DataStructureKey2D INITIAL_KEY = new DataStructureKey2D(0, 0);
	private AbstractTile initialTile;
	private GLTileFactory glTileFactory;

	public HashmapTileDataStructure2D() {
		map = new ConcurrentHashMap<DataStructureKey2D, AbstractTile>();
	}

	public void init(GLTileFactory glTileFactory, AbstractTile initialTile) {
		this.glTileFactory = glTileFactory;
		this.initialTile = initialTile;
		initialTile.setKey(INITIAL_KEY);
		map.put(INITIAL_KEY, initialTile);
	}

	public List<AbstractTile> getTilesAsList() {
		return new ArrayList<AbstractTile>(map.values());
	}

	// Old draw method to render all scenes
	public void drawAlt(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix) {
		int renderCount = 0;
		for (AbstractTile t : map.values()) {
			if (t.getModel() == null) {
				t.createModel(glTileFactory);

			}
			try {
				modelMatrix.pushMatrix();
				{
					modelMatrix.getTop().mult(objectPole.calcMatrix());
					t.getModel().draw(shader, modelMatrix, t.getPosition());
					renderCount++;
				}
				modelMatrix.popMatrix();
			} catch (NullPointerException e) {
				System.err.println("Tile Rendering failed");
				e.printStackTrace();
			}
		}
		System.out.println("Rendering = " + renderCount + " of " + map.size() + " tiles");

	}

	@Override
	public void draw(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, Player player) {

		drawSingleTile(shader, objectPole, modelMatrix, player.getCurrentTile());

		drawSingleTile(shader, objectPole, modelMatrix, getTileTop(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileRight(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileBottom(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileLeft(player.getCurrentTile()));

		drawSingleTile(shader, objectPole, modelMatrix, getTileTopRight(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileBottomRight(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileTopLeft(player.getCurrentTile()));
		drawSingleTile(shader, objectPole, modelMatrix, getTileBottomLeft(player.getCurrentTile()));
	}

	private void drawSingleTile(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, AbstractTile tile) {
		if (tile.getModel() == null) {
			tile.createModel(glTileFactory);
		}
		try {
			modelMatrix.pushMatrix();
			{
				modelMatrix.getTop().mult(objectPole.calcMatrix());
				tile.getModel().draw(shader, modelMatrix, tile.getPosition());
			}
			modelMatrix.popMatrix();
		} catch (NullPointerException e) {
			System.err.println("Tile Rendering failed");
			e.printStackTrace();
		}
	}

	@Override
	public void populateNeighbouringTiles(AbstractTile tile) {

		DataStructureKey2D parentKey = tile.getKey();
		addTile(parentKey, 0, 1, tile);
		addTile(parentKey, 1, 1, tile);
		addTile(parentKey, 1, 0, tile);
		addTile(parentKey, 1, -1, tile);
		addTile(parentKey, 0, -1, tile);
		addTile(parentKey, -1, -1, tile);
		addTile(parentKey, -1, 0, tile);
		addTile(parentKey, -1, +1, tile);
	}

	private void addTile(DataStructureKey2D parentKey, int xAdjust, int yAdjust, AbstractTile tile) {

		int adjustedX = parentKey.x + xAdjust;
		int adjustedY = parentKey.y + yAdjust;
		Vector3 tilePosition = new Vector3(adjustedX * tile.getSize(), adjustedY * tile.getSize(), 0);
		Vector3 tileAngle = new Vector3(0, 0, 0);
		GLPosition position = new GLPosition(tilePosition, tileAngle, AbstractTile.SIZE, 0);
		DataStructureKey2D key = new DataStructureKey2D(adjustedX, adjustedY);

		if (map.containsKey(key)) {
			// System.out.println("Key (" + key.x + "," + key.y +
			// ") already exists!");
		} else {
			map.put(key, new PolygonHeightmapTile(key, null, position));
		}
	}

	// @Override
	// public ArrayList<AbstractTile> getNeighbouringTiles(AbstractTile tile) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// @Override
	// public AbstractTile getTileTopRight(AbstractTile tile) {
	// return map.get(new DataStructureKey2D(tile.getKey().x + 1,
	// tile.getKey().y + 1));
	// }

	@Override
	public AbstractTile getTileTop(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottom(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x, tile.getKey().y - 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileTopLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileTopRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottomLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y - 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottomRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = map.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y - 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getInitialTile() {
		return initialTile;
	}

	// TODO: Need to handle returning the null type properly
	@Override
	public AbstractTile getTileUsingKey(DataStructureKey2D key) {
		AbstractTile tile = map.get(key);
		if (tile != null) {
			return tile;
		} else {
			return null;
		}
	}

	@Override
	public Iterator<?> getIterator() {
		return map.entrySet().iterator();
	}
}
