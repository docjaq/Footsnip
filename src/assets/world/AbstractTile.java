package assets.world;

import java.util.ArrayList;
import java.util.List;

import maths.types.Vector3;
import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import assets.Asset;
import assets.entities.Entity;
import assets.world.datastructures.DataStructureKey2D;

public abstract class AbstractTile implements Asset {

	protected GLModel model;
	protected Vector3 tilePos; // I don't like this, it should be temporary
	protected DataStructureKey2D key;

	protected List<Entity> containedEntities;

	public static final float SIZE = 1.0f;

	public AbstractTile(DataStructureKey2D key, GLModel model, Vector3 tilePos) {
		this.key = key;
		this.model = model;
		this.tilePos = tilePos;

		containedEntities = new ArrayList<Entity>();
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

	public List<Entity> getContainedEntities() {
		return containedEntities;
	}
}
