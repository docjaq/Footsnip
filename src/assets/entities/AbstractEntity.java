package assets.entities;

import renderer.glmodels.GLModel;
import assets.Asset;

/************************
 * @author docjaq This is the abstract class that all entities should subclass.
 *         So Characters, loot, etc
 */

public abstract class AbstractEntity implements Asset {

	// May or may refer to animation, whether the object can be destroyed, or
	// whether it 'physically' moves
	// private boolean dynamic;

	// Physical properties
	// private float speed;
	// private float acceleration;

	// TODO: If this is fast enough, it works nicely. If not, just make model
	// public. Same with some of the variables in GLModel Etc
	protected GLModel model;

	public AbstractEntity() {
	}

	public AbstractEntity(GLModel model) {
		this.model = model;
	}

	public GLModel getModel() {
		return model;
	}

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}

		this.model = model;
	}

	public void destroy() {
		model.cleanUp();

		// Cleanup non-gl stuff
	}

	/****************
	 * Potential methods
	 */
	// public abstract void update();
	// public abstract void checkStillAlive();
}
