package assets.world.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import math.types.MatrixStack;
import math.types.Vector3;

import org.lwjgl.opengl.GL11;

import pooling.DefaultMeshPool;
import renderer.GLPosition;
import renderer.GLUtilityMethods;
import renderer.glmodels.GLMesh;
import renderer.glshaders.GLGaussianTessellationShader;
import renderer.glshaders.GLShader;
import renderer.glshaders.GLWaterShader;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.entities.Player;
import assets.world.AbstractTile;
import assets.world.PolygonHeightmapTile;
import assets.world.PolygonHeightmapTileFactory;
import camera.CameraModel.ObjectPole;

public class HashmapTileDataStructure2D implements TileDataStructure2D {

	/*
	 * Needs to be Concurrent to allow the draw() and
	 * populateNeighbouringTiles() to play nicely. Ultimately, rendering should
	 * probably take precedence, but this seemed like a decent solution for now
	 */
	private ConcurrentHashMap<DataStructureKey2D, AbstractTile> allTiles;
	private List<AbstractTile> currentTileList;
	private List<AbstractTile> tilesJustRemoved;
	private List<AbstractTile> tilesNeedingNewModels;

	private static final DataStructureKey2D INITIAL_KEY = new DataStructureKey2D(0, 0);
	private AbstractTile initialTile;

	private PolygonHeightmapTileFactory glTileFactory;

	private DefaultMeshPool meshPool;
	private AssetContainer assContainer;

	private final List<DataStructureKey2D> OFFSETS = new ArrayList<>(Arrays.asList(new DataStructureKey2D[] { new DataStructureKey2D(0, 1),
			new DataStructureKey2D(1, 1), new DataStructureKey2D(1, 0), new DataStructureKey2D(1, -1), new DataStructureKey2D(0, -1),
			new DataStructureKey2D(-1, -1), new DataStructureKey2D(-1, 0), new DataStructureKey2D(-1, 1) }));

	public HashmapTileDataStructure2D(AssetContainer assContainer) {
		this.assContainer = assContainer;
		allTiles = new ConcurrentHashMap<DataStructureKey2D, AbstractTile>();
		tilesNeedingNewModels = new ArrayList<AbstractTile>(9);
		currentTileList = new ArrayList<AbstractTile>(9);
		tilesJustRemoved = new ArrayList<AbstractTile>(9);

		meshPool = new DefaultMeshPool(9, 16, 5, 64);
	}

	public void init(PolygonHeightmapTileFactory glTileFactory, AbstractTile initialTile) {
		this.glTileFactory = glTileFactory;
		this.initialTile = initialTile;
		initialTile.setKey(INITIAL_KEY);
		allTiles.put(INITIAL_KEY, initialTile);

		initialTile.addObserver(assContainer.getPhysicsEngine());
	}

	public List<AbstractTile> getTilesAsList() {
		ArrayList<AbstractTile> list = new ArrayList<AbstractTile>(allTiles.values());
		return list;
	}

	// Old draw method to render all scenes
	public void drawAllTiles(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix) {
		int renderCount = 0;
		for (AbstractTile t : allTiles.values()) {
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
		System.out.println("Rendering = " + renderCount + " of " + allTiles.size() + " tiles");

	}

	@Override
	public void drawTerrain(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, Player player) {

		((GLGaussianTessellationShader) shader).setTessLevelInner(1);
		((GLGaussianTessellationShader) shader).setTessLevelOuter(1);
		drawSingleTileTerrain(shader, objectPole, modelMatrix, player.getCurrentTile());

		((GLGaussianTessellationShader) shader).setTessLevelInner(1);
		((GLGaussianTessellationShader) shader).setTessLevelOuter(1);
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileTop(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileRight(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileBottom(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileLeft(player.getCurrentTile()));

		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileTopRight(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileBottomRight(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileTopLeft(player.getCurrentTile()));
		drawSingleTileTerrain(shader, objectPole, modelMatrix, getTileBottomLeft(player.getCurrentTile()));

	}

	@Override
	public void drawWater(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, Player player) {

		drawSingleTileWater(shader, objectPole, modelMatrix, player.getCurrentTile());
	}

	@Override
	public void drawEntities(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, Player player,
			Class<? extends Entity> entityClass) {

		for (AbstractTile tile : getAllNeighbouringTilesAndCurrentTileAsList(player.getCurrentTile())) {
			if (tile != null) {

				List<Entity> entities = tile.getContainedEntities();

				List<Entity> toRemove = new ArrayList<Entity>();
				for (Entity m : entities) {
					if (entityClass.isAssignableFrom(m.getClass())) {
						if (m.isDestroyable()) {
							toRemove.add(m);
						}
					}
				}
				entities.removeAll(toRemove);
				for (Entity m : entities) {
					if (entityClass.isAssignableFrom(m.getClass())) {
						modelMatrix.pushMatrix();
						{
							modelMatrix.getTop().mult(objectPole.calcMatrix());
							m.getModel().draw(shader, modelMatrix, m.getPosition());
						}
						modelMatrix.popMatrix();
					}
				}
			}
		}

	}

	private void drawSingleTileTerrain(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, AbstractTile tile) {
		if (tile != null) {

			try {
				modelMatrix.pushMatrix();
				{
					modelMatrix.getTop().mult(objectPole.calcMatrix());
					if (shader instanceof GLGaussianTessellationShader) {
						PolygonHeightmapTile polygonTile = (PolygonHeightmapTile) tile;

						// Bind heightmap texture if not already bound
						if (polygonTile.getHeightmapLocation() == -1) {
							polygonTile.setHeightmapLocation(GLUtilityMethods.bindBufferAs2DTexture(polygonTile.getHeightmapBuf(),
									GL11.GL_RED, polygonTile.getHeightmapSize(), polygonTile.getHeightmapSize()));
						}
						((GLGaussianTessellationShader) shader).setHeightmapLocation(polygonTile.getHeightmapLocation());

						if (polygonTile.getColorMapLocation() == -1) {
							polygonTile.setColorMapLocation(GLUtilityMethods.bindBufferAs1DTexture(polygonTile.getColorMap(), GL11.GL_RGBA,
									polygonTile.getColorMapSize()));
						}
						((GLGaussianTessellationShader) shader).setColorMapLocation(polygonTile.getColorMapLocation());

						// Bind normalmap texture if not already bound
						/*
						 * if (polygonTile.getNormalmapLocation() == -1) {
						 * polygonTile.setNormalmapLocation(GLUtilityMethods.
						 * bindBufferAs2DTexture(polygonTile.getNormalmapBuf(),
						 * GL11.GL_RGB, polygonTile.getNormalmapSize(),
						 * polygonTile.getNormalmapSize())); }
						 * ((GLGaussianTessellationShader)
						 * shader).setNormalmapLocation
						 * (polygonTile.getNormalmapLocation());
						 */
					}
					if (tile.getModel() != null) {
						tile.getModel().draw(shader, modelMatrix, tile.getPosition());
					}
				}
				modelMatrix.popMatrix();
			} catch (NullPointerException e) {
				System.err.println("Tile Rendering failed");
				e.printStackTrace();
			}
		} else {
			// System.out.println("Tile hasn't been created yet! - This shouldn't really be happening!");
		}
	}

	private void drawSingleTileWater(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix, AbstractTile tile) {
		PolygonHeightmapTile polyTile = (PolygonHeightmapTile) tile;
		if (tile != null) {
			if (polyTile.isWater()) {
				try {
					modelMatrix.pushMatrix();
					{
						if (shader instanceof GLWaterShader) {
							((GLWaterShader) shader).setTileIndex(tile.getKey().x, tile.getKey().y);
						}
						modelMatrix.getTop().mult(objectPole.calcMatrix());
						if (tile.getModel() != null) {
							tile.getModel().draw(shader, modelMatrix, tile.getPosition());
						}
					}
					modelMatrix.popMatrix();
				} catch (NullPointerException e) {
					System.err.println("Tile Rendering failed");
					e.printStackTrace();
				}
			}
		} else {
			// System.out.println("Tile hasn't been created yet! - This shouldn't really be happening!");
		}
	}

	@Override
	public void populateNeighbouringTiles(AbstractTile parentTile) {
		parentTile.setActive(true);

		tilesNeedingNewModels.clear();
		tilesJustRemoved.clear();
		tilesJustRemoved.addAll(currentTileList);
		currentTileList.clear();
		currentTileList.add(parentTile);

		// Add all the new tiles in parallel
		OFFSETS.parallelStream().forEach(e -> currentTileList.add(addTile(parentTile, e)));

		tilesJustRemoved.removeAll(currentTileList);
		// TilesJustRemoved now contains JUST the tiles that the player has
		// moved away from

		// Freeuptiles from pool
		for (AbstractTile t : tilesJustRemoved) {
			meshPool.returnObject((GLMesh) t.getPhysicsModel());
			// TODO: The following may already be null from the returnObject.
			// Check and remove.
			t.setPhysicsModel(null);
		}

		tilesNeedingNewModels.addAll(currentTileList);

		// Work out new tiles
		List<AbstractTile> tilesThatAlreadyHaveModels = new ArrayList<AbstractTile>();
		for (AbstractTile t : tilesNeedingNewModels) {
			// If mesh object in tile is not null, remove it from
			// currentTileList
			if (t.getPhysicsModel() != null) {
				tilesThatAlreadyHaveModels.add(t);
			}
		}
		tilesNeedingNewModels.removeAll(tilesThatAlreadyHaveModels);

		// CurrentTilesList now contains JUST the tiles that we want to get new
		// models for
		tilesNeedingNewModels.parallelStream().forEach(e -> setupPhysicsModel(e));

	}

	private void setupPhysicsModel(AbstractTile t) {
		GLMesh physicsModel = meshPool.borrowObject(((PolygonHeightmapTile) t).getHeightmap());
		physicsModel.instantiateBuffersLocally();
		t.setPhysicsModel(physicsModel);
	}

	private AbstractTile addTile(final AbstractTile parentTile, final DataStructureKey2D offset) {

		int adjustedX = parentTile.getKey().x + offset.x;
		int adjustedY = parentTile.getKey().y + offset.y;
		DataStructureKey2D key = new DataStructureKey2D(adjustedX, adjustedY);

		AbstractTile tile = null;
		if (allTiles.containsKey(key)) {
			tile = allTiles.get(key);
		} else {
			Vector3 tilePosition = new Vector3(adjustedX * parentTile.getSize(), adjustedY * parentTile.getSize(), 0);
			GLPosition position = new GLPosition(tilePosition, AbstractTile.SIZE, 0);
			tile = glTileFactory.create(key, position);
			allTiles.put(key, tile);
			tile.addObserver(assContainer.getPhysicsEngine());
		}

		tile.setActive(true);

		return tile;
	}

	@Override
	public List<AbstractTile> getAllNeighbouringTilesAndCurrentTileAsList(AbstractTile tile) {
		List<AbstractTile> tiles = new ArrayList<AbstractTile>();

		tiles.add(tile);
		tiles.add(getTileTop(tile));
		tiles.add(getTileBottom(tile));
		tiles.add(getTileLeft(tile));
		tiles.add(getTileRight(tile));
		tiles.add(getTileTopLeft(tile));
		tiles.add(getTileTopRight(tile));
		tiles.add(getTileBottomLeft(tile));
		tiles.add(getTileBottomRight(tile));

		return tiles;
	}

	@Override
	public AbstractTile getTileTop(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottom(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x, tile.getKey().y - 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileTopLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileTopRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y + 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottomLeft(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x - 1, tile.getKey().y - 1));
		} catch (NullPointerException e) {
		}
		return neighbourTile;
	}

	@Override
	public AbstractTile getTileBottomRight(AbstractTile tile) {
		AbstractTile neighbourTile = null;
		try {
			neighbourTile = allTiles.get(new DataStructureKey2D(tile.getKey().x + 1, tile.getKey().y - 1));
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
		AbstractTile tile = allTiles.get(key);
		if (tile != null) {
			return tile;
		} else {
			return null;
		}
	}

	@Override
	public Iterator<?> getIterator() {
		return allTiles.entrySet().iterator();
	}

	@Override
	public PolygonHeightmapTileFactory getGlTileFactory() {
		return glTileFactory;
	}

}
