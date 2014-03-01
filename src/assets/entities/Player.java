package assets.entities;

import main.GameControl;
import maths.LinearAlgebra;
import maths.types.Vector3;
import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;
import audio.AudioEngine;
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

	private Vector3 movementVector;

	public Vector3 getMovementVector() {
		return movementVector;
	}

	private Vector3 currentDirectionVector;

	public Player(GLModel model, String name, int age, float[] color) {
		super(model, name);
		this.age = age;
		this.color = color;

		rotationDelta = DEFAULT_ROTATION_SPEED;
		this.movementVector = new Vector3(0f, 0f, 0f);
		this.currentDirectionVector = new Vector3(0f, 0f, 0f);

		this.defaultYaw = model.modelAngle.x();
	}

	public void setModel(GLModel model) {
		super.setModel(model);
		this.defaultYaw = model.modelAngle.x();
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	public void move(int timeDelta) {
		model.modelPos.x(model.modelPos.x() + movementVector.x() * DEFAULT_MOVEMENT_SPEED * timeDelta);
		model.modelPos.y(model.modelPos.y() + movementVector.y() * DEFAULT_MOVEMENT_SPEED * timeDelta);
	}

	public void rotateCCW(int timeDelta) {
		model.modelAngle.z(model.modelAngle.z() + rotationDelta * timeDelta);
		/** Currently looks a bit crap */
		/*
		 * yawDiff -= rotationDelta; capMinYaw(); model.modelAngle.x =
		 * defaultYaw + yawDiff;
		 */
	}

	public void rotateCW(int timeDelta) {
		model.modelAngle.z(model.modelAngle.z() - rotationDelta * timeDelta);
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
		model.modelAngle.x(defaultYaw);
		// yawDiff = 0;
	}

	public void accelerateMovement() {

		AudioEngine.getInstance().playPlayerSound();

		currentDirectionVector.x((float) Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z())));
		currentDirectionVector.y((float) Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z())));
		currentDirectionVector.mult(MOVEMENT_ACCELERATION);

		movementVector.add(currentDirectionVector);
		capMaxMovementSpeed();
	}

	public void decelerateMovement() {
		currentDirectionVector.x((float) Math.cos(LinearAlgebra.degreesToRadians(model.modelAngle.z())));
		currentDirectionVector.y((float) Math.sin(LinearAlgebra.degreesToRadians(model.modelAngle.z())));
		currentDirectionVector.mult(MOVEMENT_ACCELERATION);

		movementVector.sub(currentDirectionVector);
		capMaxMovementSpeed();
	}

	private void capMaxMovementSpeed() {
		float currentSpeed = movementVector.length();
		if (currentSpeed > MAX_MOVEMENT_SPEED) {
			float diff = MAX_MOVEMENT_SPEED / currentSpeed;
			movementVector.x(movementVector.x() * diff);
			movementVector.y(movementVector.y() * diff);
		}
	}

	public Projectile fireProjectile() {

		AudioEngine.getInstance().playProjectileSound();

		Vector3 position = new Vector3(this.model.modelPos);
		Vector3 angle = new Vector3(this.model.modelAngle);
		Vector3 movementVector = new Vector3(this.movementVector);
		float scale = 1.0f;

		return new Projectile(position, angle, scale, movementVector);
	}

	/*
	 * (private void capMaxYaw() { yawDiff = Math.min(yawDiff, MAX_YAW_DIFF); }
	 * 
	 * private void capMinYaw() { yawDiff = Math.max(yawDiff, -MAX_YAW_DIFF); }
	 */

	// DEBUG: Just to debug the model geometry
	public void rotate(int timeDelta) {
		model.modelAngle.z(model.modelAngle.z() + rotationDelta * timeDelta);
		model.modelAngle.y(model.modelAngle.y() + rotationDelta * timeDelta);
		model.modelAngle.x(model.modelAngle.x() + rotationDelta * timeDelta);
	}

	@Override
	public void collidedWith(Collidable subject) {
		if (Monster.class.isAssignableFrom(subject.getClass())) {
			health--;
			System.out.printf("Health: %d\n", health);

			if (health < 1) {
				GameControl.playerDead();
			}
		}
	}

	@Override
	public void locatedWithin(AbstractTile tile, TileDataStructure2D data) {

		if (tile != currentTile) {
			if (currentTile != null) {
				currentTile.getContainedEntities().remove(this);
			}
			if (tile != null) {
				tile.getContainedEntities().add(this);
				currentTile = tile;
			}
			data.populateNeighbouringTiles(currentTile);
			System.out.println(tile.getKey().x + "," + tile.getKey().y);
		}
	}

}
