package assets.world;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import assets.Asset;
import assets.entities.Monster;
import assets.world.datastructures.DataStructureKey2D;

public abstract class AbstractTile implements Asset {

	protected GLModel model;
	protected Vector3f tilePos; // I don't like this, it should be temporary
	protected DataStructureKey2D key;

	protected Map<Integer, Monster> containedMonsters;

	public static final float SIZE = 1.0f;

	public AbstractTile(DataStructureKey2D key, GLModel model, Vector3f tilePos) {
		this.key = key;
		this.model = model;
		this.tilePos = tilePos;

		containedMonsters = new HashMap<Integer, Monster>();
	}

	public GLModel getModel() {
		return model;
	}

	public float getSize() {
		return SIZE;
	}

	public abstract void createModel(GLTileFactory glTileFactory);

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

	public Map<Integer, Monster> getContainedMonsters() {
		return containedMonsters;
	}
}
