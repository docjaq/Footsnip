package assets;

import maths.LinearAlgebra;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;

public class Player extends Character {

	private static final float DEFAULT_ROTATION_SPEED = 1.0f;
	private static final float ROTATION_ACCELERATION = 0.01f;

	private static final float DEFAULT_MOVEMENT_SPEED = 0.00f;
	private static final float MOVEMENT_ACCELERATION = 0.0001f;

	private static final float MAX_MOVEMENT_SPEED = 0.07f;

	private int age;
	private float[] color;

	// TODO: J: Clean this up I guess
	private float rotationDelta = DEFAULT_ROTATION_SPEED;
	private float scaleDelta = 0.001f;
	private float posDelta = DEFAULT_MOVEMENT_SPEED;
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

	public void move() {
		// TODO: Trig is apparently not optimal. I don't know what is...
		double xScale = Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z));
		double yScale = Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z));

		model.modelPos.x += posDelta * xScale;
		model.modelPos.y += posDelta * yScale;
	}

	/*
	 * public void moveBackward() { // TODO: Trig is apparently not optimal. I
	 * don't know what is... double xScale =
	 * Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z)); double
	 * yScale = Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z));
	 * 
	 * model.modelPos.x -= posDelta * xScale; model.modelPos.y -= posDelta *
	 * yScale; }
	 */

	public void increaseScale() {
		Vector3f.add(model.modelScale, scaleAddResolution, model.modelScale);
	}

	public void decreaseScale() {
		Vector3f.add(model.modelScale, scaleMinusResolution, model.modelScale);
	}

	public void rotateCCW() {
		model.modelAngle.z += rotationDelta;
	}

	public void rotateCW() {
		model.modelAngle.z -= rotationDelta;
	}

	public void accelerateRotation() {
		rotationDelta += ROTATION_ACCELERATION;
	}

	public void resetRotationSpeed() {
		rotationDelta = DEFAULT_ROTATION_SPEED;
	}

	public void accelerateMovement() {
		posDelta += MOVEMENT_ACCELERATION;
		capMaxMovementSpeed();
	}

	public void delerateMovement() {
		posDelta -= MOVEMENT_ACCELERATION;
		capMinMovementSpeed();
	}

	public void resetMovementSpeed() {
		posDelta = DEFAULT_MOVEMENT_SPEED;
	}

	private void capMaxMovementSpeed() {
		if (posDelta > MAX_MOVEMENT_SPEED) {
			posDelta = MAX_MOVEMENT_SPEED;
		}
	}

	// Could do this in one method, but this way means we only need one if
	// statement and or no abs() function
	private void capMinMovementSpeed() {
		if (posDelta < -MAX_MOVEMENT_SPEED) {
			posDelta = -MAX_MOVEMENT_SPEED;
		}
	}
}
