package assets.entities;

import renderer.glmodels.GLModel;
import assets.Asset;

/************************
 * @author docjaq This is the abstract class that all entities should subclass.
 *         So Characters, loot, etc
 */

public abstract class AbstractEntity implements Asset {

	private static int globalEntityIdCounter = 0;
	protected GLModel model;
	protected Integer uniqueId;

	public AbstractEntity(GLModel model) {
		this.model = model;

		uniqueId = globalEntityIdCounter;
		globalEntityIdCounter++;
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
		// TODO: Cleanup non-gl stuff
	}

	public Integer getUniqueID() {
		return uniqueId;
	}

}
