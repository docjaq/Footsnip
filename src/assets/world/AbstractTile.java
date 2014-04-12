package assets.world;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLTileFactory;
import assets.Asset;
import assets.entities.Entity;
import assets.world.datastructures.DataStructureKey2D;

public abstract class AbstractTile implements Asset {

	protected GLModel model;
	protected GLPosition position;
	// protected Vector3 tilePos; // I don't like this, it should be temporary
	protected DataStructureKey2D key;

	protected List<Entity> containedEntities;

	public static final float SIZE = 1.0f;

	public AbstractTile(DataStructureKey2D key, GLModel model, GLPosition position) {
		this.key = key;
		this.model = model;
		this.position = position;

		// Best fix for my shit code, ever.
		containedEntities = new CopyOnWriteArrayList<Entity>();
	}

	public GLModel getModel() {
		return model;
	}

	public GLPosition getPosition() {
		return position;
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
