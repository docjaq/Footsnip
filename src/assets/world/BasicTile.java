package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTilePlane;
import renderer.glshaders.GLShader;

public class BasicTile extends AbstractTile {

	/** This may not be necessary **/
	private int uniqueID;

	private float size;
	private GLShader shader;

	public BasicTile(GLModel model, float size) {
		super(model);
		this.shader = model.getShader();
		this.size = size;
	}

	public BasicTile(float size, Vector3f tilePos, GLShader shader) {
		this.size = size;
		this.shader = shader;
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		model = new GLTilePlane(tilePos, tileAngle, tileScale, shader, tileColor, this.size);
	}

	@Override
	public float getSize() {
		return size;
	}

	/*public void populateNeighbours() {
		System.out.println("Container size " + container.size());
		addTile(tile00, new Vector3f(-this.size, this.size, 0));
		addTile(tile10, new Vector3f(0, this.size, 0));
		addTile(tile20, new Vector3f(this.size, this.size, 0));
		addTile(tile01, new Vector3f(-this.size, 0, 0));
		addTile(tile21, new Vector3f(this.size, 0, 0));
		addTile(tile02, new Vector3f(-this.size, -this.size, 0));
		addTile(tile12, new Vector3f(0, -this.size, 0));
		addTile(tile22, new Vector3f(this.size, -this.size, 0));

	}*/

}
