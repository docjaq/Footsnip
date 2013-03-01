package assets;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;

public class Player extends Character {

	private int age;
	private float[] color;

	private float rotationDelta = 5.0f;
	private float scaleDelta = 0.001f;
	private float posDelta = 0.2f;
	private Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
	private Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);

	public Player(GLModel model, String name, int age, float[] color) {
		super(model, name);
		this.age = age;
		this.color = color;
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	// TODO: Move these methods to the AbstractEntity class. I have not done
	// this, as they are probably wrong. up/down/left/right are not probably the
	// final movements

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

	public void rotate() {
		model.modelAngle.z += rotationDelta;
	}

}
