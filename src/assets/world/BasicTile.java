package assets.world;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTilePlane;
import renderer.glshaders.GLShader;

public class BasicTile extends AbstractTile {

	/** This may not be necessary **/
	private int uniqueID;

	/**
	 * TODO: It seems really arbitrary to put the container and tiles in the
	 * Tile class, not the abstract tile class. But otherwise all of the tiles
	 * have to be based of AbstractTile, which makes Tile pointless. Have a
	 * think about whether the two are really necessary
	 **/

	private List<AbstractTile> container;

	// 00 10 20 /**/
	// 01 ** 21 /**/
	// 02 12 22 /**/
	private AbstractTile tile00, tile10, tile20, tile01, tile21, tile02, tile12, tile22;

	private float size;
	private GLShader shader;

	public BasicTile(List<AbstractTile> container, GLModel model, float size) {
		super(model);
		this.shader = model.getShader();
		this.size = size;
		this.container = container;
	}

	public BasicTile(List<AbstractTile> container, float size, Vector3f tilePos, GLShader shader) {
		this.container = container;
		this.size = size;
		this.shader = shader;
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		model = new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor, this.size);
	}

	/** TODO: Replace with proper data-structure **/
	public void populateNeighbours() {
		System.out.println("Container size " + container.size());
		addTile(tile00, new Vector3f(-this.size, this.size, 0));
		addTile(tile10, new Vector3f(0, this.size, 0));
		addTile(tile20, new Vector3f(this.size, this.size, 0));
		addTile(tile01, new Vector3f(-this.size, 0, 0));
		addTile(tile21, new Vector3f(this.size, 0, 0));
		addTile(tile02, new Vector3f(-this.size, -this.size, 0));
		addTile(tile12, new Vector3f(0, -this.size, 0));
		addTile(tile22, new Vector3f(this.size, -this.size, 0));

		// tile00.setNeighbours(null, null, null, null, tile10, null, tile01,
		// this);
		// tile10.setNeighbours(null, null, null, tile00, tile20, tile01, this,
		// tile21);
		// tile20.setNeighbours(tile00, tile10, tile20, tile01, tile21, tile02,
		// tile12, tile22);

	}

	/** TODO: Replace with proper data-structure **/
	private void setNeighbours(BasicTile tile00, BasicTile tile10, BasicTile tile20, BasicTile tile01, BasicTile tile21, BasicTile tile02,
			BasicTile tile12, BasicTile tile22) {
		setTile00(tile00);
		setTile10(tile10);
		setTile20(tile20);
		setTile01(tile01);
		setTile21(tile21);
		setTile02(tile02);
		setTile12(tile12);
		setTile22(tile22);
	}

	private void addTile(AbstractTile tile, Vector3f location) {
		if (tile == null) {
			tile = new BasicTile(container, size, location, shader);
			container.add(tile);
		}
	}

	public AbstractTile getTile00() {
		return tile00;
	}

	public void setTile00(AbstractTile tile00) {
		this.tile00 = tile00;
	}

	public AbstractTile getTile10() {
		return tile10;
	}

	public void setTile10(AbstractTile tile10) {
		this.tile10 = tile10;
	}

	public AbstractTile getTile20() {
		return tile20;
	}

	public void setTile20(AbstractTile tile20) {
		this.tile20 = tile20;
	}

	public AbstractTile getTile01() {
		return tile01;
	}

	public void setTile01(AbstractTile tile01) {
		this.tile01 = tile01;
	}

	public AbstractTile getTile21() {
		return tile21;
	}

	public void setTile21(AbstractTile tile21) {
		this.tile21 = tile21;
	}

	public AbstractTile getTile02() {
		return tile02;
	}

	public void setTile02(AbstractTile tile02) {
		this.tile02 = tile02;
	}

	public AbstractTile getTile12() {
		return tile12;
	}

	public void setTile12(AbstractTile tile12) {
		this.tile12 = tile12;
	}

	public AbstractTile getTile22() {
		return tile22;
	}

	public void setTile22(AbstractTile tile22) {
		this.tile22 = tile22;
	}

}
