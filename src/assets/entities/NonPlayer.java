package assets.entities;

import renderer.GLPosition;
import renderer.glmodels.GLModel;

public abstract class NonPlayer extends Entity {

	protected int health = 30;
	protected float mass;

	public NonPlayer(GLModel model, GLPosition position, float mass) {
		super(model, position, mass);
	}

	public void modifyHealth(int modification) {
		health += modification;
	}

	public int getHealth() {
		return health;
	}

}