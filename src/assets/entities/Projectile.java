package assets.entities;

import javax.vecmath.Vector3f;

import math.types.Matrix4;
import math.types.Vector3;
import math.types.Vector4;
import renderer.GLPosition;
import renderer.GLWorld;
import renderer.glmodels.GLModel;
import collision.Collidable;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.DefaultMotionState;

public class Projectile extends Entity {

	public static final int DAMAGE = 10;

	public static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	public static final float ADDITIVE_VELOCITY_SCALE = 50.00f;

	public static final long MAXIMUM_AGE = 1000;

	private long spawnTime;
	private float[] color;

	private Vector3 movementVector;
	private Player player;

	private boolean hasFired;

	public Projectile(GLModel model, Player player) {
		super(model, null);
		this.player = player;

		// Set position and movement vector from player
		Vector3 projPosition = new Vector3(player.position.modelPos);
		Vector3 projAngle = new Vector3(player.position.modelAngle);
		float projScale = 1.0f;
		this.movementVector = new Vector3(player.getMovementVector());
		this.position = new GLPosition(projPosition, projAngle, projScale, 0);

		this.position.setEntityRadiusWithModelRadius(this.model.getModelRadius());
		this.spawnTime = System.currentTimeMillis();

		Vector4 additiveMovement = new Vector4(ADDITIVE_VELOCITY_SCALE, 0.0f, 0.0f, 1.0f);

		Matrix4 rotationMatrix = new Matrix4().clearToIdentity();
		rotationMatrix.rotateDeg(position.modelAngle.z(), GLWorld.BASIS_Z);
		rotationMatrix.rotateDeg(position.modelAngle.y(), GLWorld.BASIS_Y);
		rotationMatrix.rotateDeg(position.modelAngle.x(), GLWorld.BASIS_X);

		additiveMovement = rotationMatrix.mult(additiveMovement);

		Vector3 vec3fAdditiveMovement = new Vector3(additiveMovement.x(), additiveMovement.y(), additiveMovement.z());

		movementVector.add(vec3fAdditiveMovement);

		position.modelPos.z(position.modelPos.z() - 0.01f);
		position.modelPos.add(vec3fAdditiveMovement.normalize().mult(0.03f));

		setChanged();

	}

	public long getAge() {
		return System.currentTimeMillis() - spawnTime;
	}

	public float[] getColor() {
		return color;
	}

	@Override
	public void collidedWith(final Collidable subject, final Vector3 collisionNormal) {
		if (!destroyable) {
			// Lock the subject so that multiple fast collisions (faster than
			// the rendering thread) don't cause monster health to be reduced
			// too often
			synchronized (subject) {
				// If it hits a monster
				if (Monster.class.isAssignableFrom(subject.getClass())) {
					// System.out.println("Hit");
					// ((Monster) subject).modifyHealth(-DAMAGE);
					destroyable = true;
				}
			}

		}
	}

	@Override
	public void physicalStep(CollisionObject collisionObject) {

		// TODO: Probably check which tile we're in, and if null, don't
		// simulate, like monster
		if (getAge() > Projectile.MAXIMUM_AGE) {
			destroyable = true;
		} else {
			if (rigidBody.getActivationState() == 0) {
				rigidBody.setActivationState(1);
			}
			rigidBody.activate();

			if (rigidBody != null && rigidBody.getMotionState() != null) {
				DefaultMotionState myMotionState = (DefaultMotionState) rigidBody.getMotionState();
				physicsTransform.set(myMotionState.graphicsWorldTrans);
			} else {
				collisionObject.getWorldTransform(physicsTransform);
			}

			if (!getHasFired()) {
				Vector3 force = getMovementVector();
				Vector3f impulse = new Vector3f(force.x(), force.y(), force.z());
				Vector3f playerVelocity = new Vector3f();
				player.getRigidBody().getLinearVelocity(playerVelocity);
				impulse.scale(0.01f);
				impulse.add(playerVelocity);
				rigidBody.applyCentralImpulse(impulse);
				setHasFired(true);
			}

			// Force the body to hover on a plane. May cause
			// z-oscillations; I don't fucking know, I'm not a
			// physicist.
			rigidBody.applyCentralImpulse(new Vector3f(0, 0, 0 - physicsTransform.origin.z));

			// Update its rendering position
			getPosition().setModelPos(new Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
		}
	}

	public Vector3 getMovementVector() {
		return movementVector;
	}

	public boolean getHasFired() {
		return hasFired;
	}

	public void setHasFired(boolean hasFired) {
		this.hasFired = hasFired;
	}
}
