package assets.world;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLTileFactory;
import assets.Asset;
import assets.entities.Entity;
import assets.world.datastructures.DataStructureKey2D;

import com.bulletphysics.dynamics.RigidBody;

public abstract class AbstractTile extends Observable implements Asset {

	protected GLModel model;
	protected GLModel physicsModel;
	protected GLPosition position;
	protected DataStructureKey2D key;

	protected List<Entity> containedEntities;

	public static final float SIZE = 1.0f;

	private boolean isActive;

	// Physics stuff
	private RigidBody rigidBody;

	public AbstractTile(DataStructureKey2D key, GLModel model, GLPosition position) {
		this.key = key;
		this.model = model;
		this.position = position;

		// Best fix for my shit code, ever.
		// Solves some concurrency issue. When rendering. Look into why; I
		// forget
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
		// TODO: Check that this is OK to comment out
		// if (this.model != null) {
		// throw new RuntimeException("You can only set the model once.");
		// }
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public GLModel getPhysicsModel() {
		return physicsModel;
	}

	public void setPhysicsModel(GLModel physicsModel) {
		this.physicsModel = physicsModel;
		setChanged();
		notifyObservers(physicsModel);
	}

	public RigidBody getRigidBody() {
		return rigidBody;
	}

	public void setRigidBody(RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}
}
