package assets.entities;

import main.GameControl;
import maths.LinearAlgebra;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure;
import collision.Collidable;

public class Player extends Entity {

	private static final float DEFAULT_ROTATION_SPEED = 0.1f;
	private static final float ROTATION_ACCELERATION = 0.001f;

	private static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	private static final float MOVEMENT_ACCELERATION = 0.5f;

	private static final float MAX_MOVEMENT_SPEED = 100f;

	private float defaultYaw;
	/*
	 * private float yawDiff; private static final float MAX_YAW_DIFF = 45f;
	 */

	private int age;
	private float[] color;

	private int health = 100;

	private float rotationDelta;

	private Vector3f movementVector;

	public Vector3f getMovementVector() {
		return movementVector;
	}

	private Vector3f currentDirectionVector;

	public Player(GLModel model, String name, int age, float[] color) {
		super(model, name);
		this.age = age;
		this.color = color;

		rotationDelta = DEFAULT_ROTATION_SPEED;
		this.movementVector = new Vector3f(0f, 0f, 0f);
		this.currentDirectionVector = new Vector3f(0f, 0f, 0f);

		this.defaultYaw = model.modelAngle.x;
	}

	public void setModel(GLModel model) {
		super.setModel(model);
		this.defaultYaw = model.modelAngle.x;
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	public void move(int timeDelta) {
		model.modelPos.x += movementVector.x * DEFAULT_MOVEMENT_SPEED * timeDelta;
		model.modelPos.y += movementVector.y * DEFAULT_MOVEMENT_SPEED * timeDelta;
	}

	public void rotateCCW(int timeDelta) {
		model.modelAngle.z += rotationDelta * timeDelta;
		/** Currently looks a bit crap */
		/*
		 * yawDiff -= rotationDelta; capMinYaw(); model.modelAngle.x =
		 * defaultYaw + yawDiff;
		 */
	}

	public void rotateCW(int timeDelta) {
		model.modelAngle.z -= rotationDelta * timeDelta;
		/** Currently looks a bit crap */
		/*
		 * yawDiff += rotationDelta; capMaxYaw(); model.modelAngle.x =
		 * defaultYaw + yawDiff;
		 */
	}

	public void accelerateRotation() {
		rotationDelta += ROTATION_ACCELERATION;
	}

	public void resetRotationSpeed() {
		rotationDelta = DEFAULT_ROTATION_SPEED;
		model.modelAngle.x = defaultYaw;
		// yawDiff = 0;
	}

	public void accelerateMovement() {
		currentDirectionVector.x = (float) Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z));
		currentDirectionVector.y = (float) Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z));
		currentDirectionVector.scale(MOVEMENT_ACCELERATION);

		Vector3f.add(movementVector, currentDirectionVector, movementVector);
		capMaxMovementSpeed();
	}

	public void decelerateMovement() {
		currentDirectionVector.x = (float) Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z));
		currentDirectionVector.y = (float) Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z));
		currentDirectionVector.scale(MOVEMENT_ACCELERATION);

		Vector3f.sub(movementVector, currentDirectionVector, movementVector);
		capMaxMovementSpeed();
	}

	private void capMaxMovementSpeed() {
		float currentSpeed = movementVector.length();
		if (currentSpeed > MAX_MOVEMENT_SPEED) {
			float diff = MAX_MOVEMENT_SPEED / currentSpeed;
			movementVector.x *= diff;
			movementVector.y *= diff;
		}
	}

	/*
	 * (private void capMaxYaw() { yawDiff = Math.min(yawDiff, MAX_YAW_DIFF); }
	 * 
	 * private void capMinYaw() { yawDiff = Math.max(yawDiff, -MAX_YAW_DIFF); }
	 */

	// DEBUG: Just to debug the model geometry
	public void rotate(int timeDelta) {
		model.modelAngle.z += rotationDelta * timeDelta;
		model.modelAngle.y += rotationDelta * timeDelta;
		model.modelAngle.x += rotationDelta * timeDelta;
	}

	@Override
	public void collidedWith(Collidable subject) {
		// DAVE: Isn't this just a neater way of saying
		// Monster.class.isAssignableFrom(subject.getClass())?
		if (Monster.class.isInstance(subject)) {
			health--;
			System.out.printf("Health: %d\n", health);

			if (health < 1) {
				GameControl.playerDead();
			}
		}
	}

	@Override
	public void locatedWithin(AbstractTile tile, TileDataStructure data) {
		if (tile != currentTile) {
			currentTile = tile;
			System.out.println(tile.getKey().x + "," + tile.getKey().y);
			data.populateNeighbouringTiles(currentTile);
		}
	}

}
