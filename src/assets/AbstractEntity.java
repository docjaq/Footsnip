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

	public AbstractEntity() {
		// Default constructor.
	}

	public AbstractEntity(GLModel model) {
		this.model = model;
	}

	// TODO: If this is fast enough, it works nicely. If not, just make model
	// public. Same with some of the variables in GLModel Etc
	protected GLModel model;

	public GLModel getModel() {
		return model;
	}

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}

		this.model = model;
	}

	// public void draw() {
	// model.draw();
	// }

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
