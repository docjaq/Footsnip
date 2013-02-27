package assets;

import renderer.glmodels.GLModel;

/************************
 * @author docjaq This is the abstract class that all entities should subclass.
 *         So Characters, loot, etc
 */

public abstract class AbstractEntity implements Asset {

	// This was moved into the geometry.
	// public Position position;

	// May or may refer to animation, whether the object can be destroyed, or
	// whether it 'physically' moves
	private boolean dynamic;

	// Physical properties
	private float speed;
	private float acceleration;
	private float size;

	public AbstractEntity(GLModel model) {
		this.model = model;
	}

	private GLModel model;

	public GLModel getModel() {
		return model;
	}

	public void draw() {
		model.draw();
	}

	/****************
	 * Potential methods
	 */
	// public abstract void update();
	// public abstract void checkStillAlive();
}
