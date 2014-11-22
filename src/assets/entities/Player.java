package assets.entities;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import main.GameControl;
import math.LinearAlgebra;
import math.types.Vector3;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLDefaultProjectileFactory;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;
import audio.AudioEngine;
import collision.Collidable;

public class Player extends Entity {

	private static final float DEFAULT_ROTATION_SPEED = 0.1f;
	private static final float ROTATION_ACCELERATION = 0.001f;
	public static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	private static final float MOVEMENT_ACCELERATION = 0.5f;
	public static final float MAX_MOVEMENT_SPEED = 1.5f;

	private float defaultYaw;
	private int age;
	private float[] color;
	private int health = 100;
	private float rotationDelta;
	private Vector3 movementVector;

	// This queue takes all the input from the control thread, converted into
	// scaled vectors in this class, and stores them to be used by the physics
	// engine
	// TODO: Probably turn this into a pool, to save on memory usage
	private Queue<Vector3> controlInputMovement;

	// private Vector3 currentDirectionVector;

	public Player(GLModel model, GLPosition position, int age, float[] color) {
		super(model, position);
		this.age = age;
		this.color = color;

		rotationDelta = DEFAULT_ROTATION_SPEED;
		this.movementVector = new Vector3(0f, 0f, 0f);
		// this.currentDirectionVector = new Vector3(0f, 0f, 0f);

		this.defaultYaw = position.modelAngle.x();

		controlInputMovement = new ArrayBlockingQueue<Vector3>(20);

		setChanged();
	}

	public void setModel(GLModel model) {
		super.setModel(model);
		this.defaultYaw = position.modelAngle.x();
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	// public void move(int timeDelta) {
	// position.modelPos.x(position.modelPos.x() + movementVector.x() *
	// DEFAULT_MOVEMENT_SPEED * timeDelta);
	// position.modelPos.y(position.modelPos.y() + movementVector.y() *
	// DEFAULT_MOVEMENT_SPEED * timeDelta);
	// }

	public void rotateCCW(int timeDelta) {
		position.modelAngle.z(position.modelAngle.z() + rotationDelta * timeDelta);
		/** Currently looks a bit crap */
		/*
		 * yawDiff -= rotationDelta; capMinYaw(); model.modelAngle.x =
		 * defaultYaw + yawDiff;
		 */
	}

	public void rotateCW(int timeDelta) {
		position.modelAngle.z(position.modelAngle.z() - rotationDelta * timeDelta);
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
		position.modelAngle.x(defaultYaw);
		// yawDiff = 0;
	}

	public void accelerateMovement() {

		AudioEngine.getInstance().playPlayerSound();

		Vector3 movement = new Vector3();
		movement.x((float) Math.cos(LinearAlgebra.degreesToRadians(position.modelAngle.z())));
		movement.y((float) Math.sin(LinearAlgebra.degreesToRadians(position.modelAngle.z())));
		movement.mult(MOVEMENT_ACCELERATION);

		controlInputMovement.add(movement);

		// movementVector.add(movement);
		// capMaxMovementSpeed();
	}

	public void decelerateMovement() {

		Vector3 movement = new Vector3();
		movement.x(-(float) Math.cos(LinearAlgebra.degreesToRadians(position.modelAngle.z())));
		movement.y(-(float) Math.sin(LinearAlgebra.degreesToRadians(position.modelAngle.z())));
		movement.mult(MOVEMENT_ACCELERATION);

		controlInputMovement.add(movement);

		// movementVector.sub(movement);
		// capMaxMovementSpeed();
	}

	// private void capMaxMovementSpeed() {
	// float currentSpeed = movementVector.length();
	// if (currentSpeed > MAX_MOVEMENT_SPEED) {
	// float diff = MAX_MOVEMENT_SPEED / currentSpeed;
	// movementVector.x(movementVector.x() * diff);
	// movementVector.y(movementVector.y() * diff);
	// }
	// }

	public Projectile fireProjectile() {

		AudioEngine.getInstance().playProjectileSound();

		Vector3 projPosition = new Vector3(this.position.modelPos);
		Vector3 projAngle = new Vector3(this.position.modelAngle);
		float projScale = 1.0f;

		Vector3 movementVector = new Vector3(this.movementVector);

		GLPosition position = new GLPosition(projPosition, projAngle, projScale, 0);

		return new Projectile(GLDefaultProjectileFactory.getInstance().create(), position, movementVector);
	}

	/*
	 * (private void capMaxYaw() { yawDiff = Math.min(yawDiff, MAX_YAW_DIFF); }
	 * 
	 * private void capMinYaw() { yawDiff = Math.max(yawDiff, -MAX_YAW_DIFF); }
	 */

	// DEBUG: Just to debug the model geometry
	public void rotate(int timeDelta) {
		position.modelAngle.z(position.modelAngle.z() + rotationDelta * timeDelta);
		position.modelAngle.y(position.modelAngle.y() + rotationDelta * timeDelta);
		position.modelAngle.x(position.modelAngle.x() + rotationDelta * timeDelta);
	}

	@Override
	public void collidedWith(final Collidable subject, final Vector3 collisionNormal) {
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
			// This method actually checks if the tiles already exist before
			// population.
			data.populateNeighbouringTiles(currentTile);
			System.out.println(tile.getKey().x + "," + tile.getKey().y);
		}
	}

	public Queue<Vector3> getControlInputMovement() {
		return controlInputMovement;
	}

	public Vector3 getMovementVector() {
		return movementVector;
	}
}
