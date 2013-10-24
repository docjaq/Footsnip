package assets.entities;

import renderer.glmodels.GLModel;
import collision.Collidable;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Entity {

	private int level;
	private final int MOVEMENT_DELTA = (int) (Math.random() * 4f);

	private float ROTATION_DELTA = (float) Math.random() * 1.0f;

	public void setRotationDelta(float rotationDelta) {
		// TODO: Dividing this by 30 seems a bit arbitrary, but we're going to
		// multiply by the time delta later, which is typically about 30.
		this.ROTATION_DELTA = rotationDelta / 20.0f;
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
		model.modelAngle.z += ROTATION_DELTA * timeDelta;
		model.modelAngle.y += ROTATION_DELTA * timeDelta;
		model.modelAngle.x += ROTATION_DELTA * timeDelta;
	}

	@Override
	public void collidedWith(Collidable subject) {
		if (Player.class.isAssignableFrom(subject.getClass())) {
			ROTATION_DELTA *= 1.01;
		}
	}
}
