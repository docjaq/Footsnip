package assets.entities;

import java.util.Observable;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import assets.Asset;

public abstract class AbstractEntity extends Observable implements Asset {

	private static int globalEntityIdCounter = 0;
	protected GLModel model;
	protected GLPosition position;
	protected Integer uniqueId;

	public AbstractEntity(GLModel model, GLPosition position) {
		this.model = model;
		this.position = position;

		uniqueId = globalEntityIdCounter;
		globalEntityIdCounter++;
	}

	public GLModel getModel() {
		return model;
	}

	public GLPosition getPosition() {
		return position;
	}

	public void setModel(GLModel model) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}

		this.model = model;
	}

	public void destroy() {
		notifyObservers(model);
		model.cleanUp();
		// TODO: Cleanup non-gl stuff
	}

	public Integer getUniqueID() {
		return uniqueId;
	}

}
