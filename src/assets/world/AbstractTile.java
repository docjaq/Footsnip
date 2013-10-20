package assets.world;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import renderer.glshaders.GLShader;
import assets.Asset;
import assets.world.datastructures.DataStructureKey2D;

public abstract class AbstractTile implements Asset {

	protected GLModel model;
	protected Vector3f tilePos; // I don't like this, it should be temporary
	protected DataStructureKey2D key;
	public static final float SIZE = 1.0f;

	public AbstractTile() {
	}

	public AbstractTile(DataStructureKey2D key, GLModel model, Vector3f tilePos) {
		this.key = key;
		this.model = model;
		this.tilePos = tilePos;
	}

	public GLModel getModel() {
		return model;
	}

	public float getSize() {
		return SIZE;
	}

	public abstract void createModel(GLTileFactory glTileFactory, GLShader shader);

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		this.model = model;
	}

	public void destroy() {
		model.cleanUp();
	}

	public DataStructureKey2D getKey() {
		return key;
	}

	public void setKey(DataStructureKey2D key) {
		this.key = key;
	}
}
