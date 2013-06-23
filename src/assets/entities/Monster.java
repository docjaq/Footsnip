package assets.entities;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Character {

	private int level;

	private float rotationDelta = 0.5f;

	public void setRotationDelta(float rotationDelta) {
		// TODO: Dividing this by 30 seems a bit arbitrary, but we're going to
		// multiply by the time delta later, which is typically about 30.
		this.rotationDelta = rotationDelta / 30.0f;
	}

	private float scaleDelta = 0.001f;
	private float posDelta = 0.1f;
	private Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
	private Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);

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

	public void increaseScale() {
		Vector3f.add(model.modelScale, scaleAddResolution, model.modelScale);
	}

	public void decreaseScale() {
		Vector3f.add(model.modelScale, scaleMinusResolution, model.modelScale);
	}

	public void rotate(int timeDelta) {
		model.modelAngle.z += rotationDelta * timeDelta;
		model.modelAngle.y += rotationDelta * timeDelta;
		model.modelAngle.x += rotationDelta * timeDelta;
	}
}
