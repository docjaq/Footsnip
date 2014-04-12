package assets.entities;

import renderer.GLPosition;
import renderer.glmodels.GLModel;
import audio.AudioEngine;
import collision.Collidable;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Entity {

	private int level;
	private final int MOVEMENT_DELTA = (int) (Math.random() * 4f);
	private final float POS_DELTA = (float) Math.random() * 0.0004f;
	private float rotationDelta = (float) Math.random() * 1.0f;

	private int health = 30;

	public void setRotationDelta(float rotationDelta) {
		// TODO: Dividing this by 30 seems a bit arbitrary, but we're going to
		// multiply by the time delta later, which is typically about 30.
		this.rotationDelta = rotationDelta / 20.0f;
	}

	public Monster(GLModel model, GLPosition position, int level) {
		super(model, position);
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void moveLeft() {
		position.modelPos.x(position.modelPos.x() - POS_DELTA);
	}

	public void moveRight() {
		position.modelPos.x(position.modelPos.x() + POS_DELTA);
	}

	public void moveUp() {
		position.modelPos.y(position.modelPos.y() + POS_DELTA);
	}

	public void moveDown() {
		position.modelPos.y(position.modelPos.y() - POS_DELTA);
	}

	public void moveRandom() {
		switch (MOVEMENT_DELTA) {
		case 0:
			moveLeft();
			break;
		case 1:
			moveRight();
			break;
		case 2:
			moveUp();
			break;
		case 3:
			moveDown();
			break;
		}
	}

	/**
	 * If re-enabling, modelScale is now private and should be set with a single
	 * float
	 **/
	/*
	 * public void increaseScale() { Vector3f.add(model.modelScale,
	 * scaleAddResolution, model.modelScale); }
	 * 
	 * public void decreaseScale() { Vector3f.add(model.modelScale,
	 * scaleMinusResolution, model.modelScale); }
	 */

	public void rotate(int timeDelta) {
		position.modelAngle.z(position.modelAngle.z() + rotationDelta * timeDelta);
		position.modelAngle.y(position.modelAngle.y() + rotationDelta * timeDelta);
		position.modelAngle.x(position.modelAngle.x() + rotationDelta * timeDelta);
	}

	@Override
	public void collidedWith(Collidable subject) {
		if (Player.class.isAssignableFrom(subject.getClass())) {
			rotationDelta *= 1.01;
		}
		if (Monster.class.isAssignableFrom(subject.getClass())) {
			// TODO: Do something
		}
	}

	public void modifyHealth(int modification) {
		health += modification;
	}

	@Override
	public boolean isDestroyable() {

		if (health <= 0) {
			AudioEngine.getInstance().playMonsterSound(getPosition());
			destroyable = true;
		}

		// /if (destroyable) {
		// destroy();
		// }

		return destroyable;
	}

	public int getHealth() {
		return health;
	}
}
