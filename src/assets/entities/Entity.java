package assets.entities;

import maths.LinearAlgebra;
import renderer.glmodels.GLModel;

public class Entity extends AbstractEntity {

	private String name;

	public Entity(GLModel model, String name) {
		super(model);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getEuclideanDistance(Entity other) {
		return LinearAlgebra.euclideanDistance(this.model.modelPos, other.model.modelPos);
	}
}
