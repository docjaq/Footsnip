package assets.entities;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.vecmath.Vector3f;

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

import com.bulletphysics.linearmath.DefaultMotionState;

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
		this.mass = 1.0f;

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

		return new Projectile(GLDefaultProjectileFactory.getInstance().create(), this);
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
		if (Asteroid.class.isAssignableFrom(subject.getClass())) {
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

	@Override
	public void physicalStep() {
		// Check here, as if things are initialised late, can cause
		// a problem
		if (rigidBody.getActivationState() == 0) {

			rigidBody.setActivationState(1);
		}
		rigidBody.activate();

		// TODO: Clean this up so A) it uses a pool not a queue, and
		// B) don't have to fucking convert vector formats
		Vector3f velocity = new Vector3f();
		while (!getControlInputMovement().isEmpty()) {
			Vector3 vec = getControlInputMovement().remove();
			velocity.x += vec.x() * 4;
			velocity.y += vec.y() * 4;
			velocity.z += vec.z() * 4;
		}

		// Limit maximum speed
		rigidBody.applyCentralForce(velocity);
		rigidBody.getLinearVelocity(velocity);
		float speed = velocity.length();
		if (speed > Player.MAX_MOVEMENT_SPEED) {
			velocity.scale(Player.MAX_MOVEMENT_SPEED / speed);
			rigidBody.setLinearVelocity(velocity);
		}

		if (rigidBody != null && rigidBody.getMotionState() != null) {
			DefaultMotionState myMotionState = (DefaultMotionState) rigidBody.getMotionState();
			physicsTransform.set(myMotionState.graphicsWorldTrans);
		} else {
			rigidBody.getWorldTransform(physicsTransform);
		}

		// Force the body to hover on a plane. May cause
		// z-oscillations; I don't fucking know, I'm not a
		// physicist.
		rigidBody.applyCentralImpulse(new Vector3f(0, 0, 0 - physicsTransform.origin.z));

		// Update its rendering position
		getPosition().setModelPos(new Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
	}

	public Queue<Vector3> getControlInputMovement() {
		return controlInputMovement;
	}

	public Vector3 getMovementVector() {
		return movementVector;
	}
}
