package assets.world;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTilePlane;
import renderer.glshaders.GLShader;

public class Tile extends AbstractTile {

	/** This may not be necessary **/
	private int uniqueID;

	/**
	 * TODO: It seems really arbitrary to put the container and tiles in the
	 * Tile class, not the abstract tile class. But otherwise all of the tiles
	 * have to be based of AbstractTile, which makes Tile pointless. Have a
	 * think about whether the two are really necessary
	 **/

	private List<Tile> container;

	// 00 10 20 /**/
	// 01 ** 21 /**/
	// 02 12 22 /**/
	private Tile tile00, tile10, tile20, tile01, tile21, tile02, tile12, tile22;

	private float size;
	private GLShader shader;

	public Tile(List<Tile> container, GLModel model, float size) {
		super(model);
		this.shader = model.getShader();
		this.size = size;
		this.container = container;
	}

	public Tile(List<Tile> container, float size, Vector3f tilePos, GLShader shader) {
		this.size = size;
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		model = new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor, this.size);
	}

	public void populateNeighbours() {
		tile01 = new Tile(container, size, new Vector3f(-this.size, 0, 0), shader);
		tile12 = new Tile(container, size, new Vector3f(0, -this.size, 0), shader);
		// Add the rest

		container.add(tile01);
		container.add(tile12);
	}

}
