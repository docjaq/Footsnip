package assets.entities;

import renderer.glmodels.GLModel;
import collision.Collidable;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Entity {

	private int level;
	private final int MOVEMENT_DIRECTION = (int) (Math.random() * 4f);

	private float rotationDelta = 0.5f;

	public void setRotationDelta(float rotationDelta) {
		// TODO: Dividing this by 30 seems a bit arbitrary, but we're going to
		// multiply by the time delta later, which is typically about 30.
		this.rotationDelta = rotationDelta / 30.0f;
	}

	// private float scaleDelta = 0.001f;
	private float posDelta = (float) Math.random() * 0.0001f;

	// private Vector3f scaleAddResolution = new Vector3f(scaleDelta,
	// scaleDelta, scaleDelta);
	// private Vector3f scaleMinusResolution = new Vector3f(-scaleDelta,
	// -scaleDelta, -scaleDelta);

	public Monster(GLModel model, String name, int level) {
		super(model, name);
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void moveLeft() {
		model.modelPos.x -= posDelta;
	}

	public void moveRight() {
		model.modelPos.x += posDelta;
	}

	public void moveUp() {
		model.modelPos.y += posDelta;
	}

	public void moveDown() {
		model.modelPos.y -= posDelta;
	}

	public void moveRandom() {
		switch (MOVEMENT_DIRECTION) {
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
		model.modelAngle.z += rotationDelta * timeDelta;
		model.modelAngle.y += rotationDelta * timeDelta;
		model.modelAngle.x += rotationDelta * timeDelta;
	}

	@Override
	public void collidedWith(Collidable subject) {
		if (Player.class.isAssignableFrom(subject.getClass())) {
			rotationDelta *= 1.01;
		}
	}
}
