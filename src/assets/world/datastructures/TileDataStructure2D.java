package assets.world.datastructures;

import java.util.Iterator;
import java.util.List;

import maths.types.MatrixStack;
import renderer.glmodels.GLTileFactory;
import renderer.glshaders.GLShader;
import util.MousePoles.ObjectPole;
import assets.world.AbstractTile;

public interface TileDataStructure2D {

	public void init(GLTileFactory glTileFactory, AbstractTile tile);

	/** TODO: Consider removing this **/
	public List<AbstractTile> getTilesAsList();

	public void populateNeighbouringTiles(AbstractTile tile);

	/**
	 * IMPORTANT NOTE: This draw method now must check to see if the model
	 * exists. If it doesn't, it must create it. The reason for this is that
	 * ONLY the LWJGL thread can access the OpenGL context. So if the game
	 * thread (that handles modifications to the Player object) tries to
	 * populateNeighbourTiles(), this tries to create a GLModel. So, rather,
	 * now, it creates a Tile, but the tile has a null GLModel. And then, when
	 * the draw method is called, it grabs each tile, and first it checks to
	 * make sure that tile has a GLModel. If not, it instantiates it. As this is
	 * called from the Renderer_3_2 thread, this is allowed. I did this a bit
	 * hastily, as I'd written all the other code, and wanted to get it working.
	 * Maybe re-think how/where/when this happens, as it's not obvious to
	 * someone adding a new data-structure that they must check to see if a tile
	 * is not null
	 **/
	public void draw(GLShader shader, ObjectPole objectPole, MatrixStack modelMatrix);

	public AbstractTile getInitialTile();

	public AbstractTile getTileUsingKey(DataStructureKey2D key);

	public AbstractTile getTileTop(AbstractTile tile);

	public AbstractTile getTileRight(AbstractTile tile);

	public AbstractTile getTileLeft(AbstractTile tile);

	public AbstractTile getTileBottom(AbstractTile tile);

	// Need to be able to cleanly iterate through tiles. This is the best way
	// that I could think of to do it quickly and independent of the
	// data-structure

	public Iterator getIterator();

}
