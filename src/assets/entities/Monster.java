package assets.entities;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import audio.AudioEngine;
import collision.Collidable;

import com.bulletphysics.linearmath.DefaultMotionState;

//This should probably be a class NPC, which Monster then extends, but decided
//to simplify it
public class Monster extends Entity {

	private int level;
	private final int MOVEMENT_DELTA = (int) (Math.random() * 4f);
	private final float POS_DELTA = (float) Math.random() * 0.0004f;
	private float rotationDelta = (float) Math.random() * 1.0f;

	private int health = 30;

	public void setRotationDelta(float rotationDelta) {
		// TODO: Dividing this by 30 seems a bit arbitrary, but we're going to
		// multiply by the time delta later, which is typically about 30.
		this.rotationDelta = rotationDelta / 20.0f;
	}

	public Monster(GLModel model, GLPosition position, int level) {
		super(model, position);
		this.level = level;
		this.mass = 4f;
		setChanged();
	}

	public int getLevel() {
		return level;
	}

	public void moveLeft() {
		position.modelPos.x(position.modelPos.x() - POS_DELTA);
	}

	public void moveRight() {
		position.modelPos.x(position.modelPos.x() + POS_DELTA);
	}

	public void moveUp() {
		position.modelPos.y(position.modelPos.y() + POS_DELTA);
	}

	public void moveDown() {
		position.modelPos.y(position.modelPos.y() - POS_DELTA);
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
		position.modelAngle.z(position.modelAngle.z() + rotationDelta * timeDelta);
		position.modelAngle.y(position.modelAngle.y() + rotationDelta * timeDelta);
		position.modelAngle.x(position.modelAngle.x() + rotationDelta * timeDelta);
	}

	@Override
	public void collidedWith(final Collidable subject, final Vector3 collisionNormal) {

		if (Player.class.isAssignableFrom(subject.getClass())) {
			rotationDelta *= 2;
		}
		if (Monster.class.isAssignableFrom(subject.getClass())) {
			// TODO: Do something
		}
		if (Projectile.class.isAssignableFrom(subject.getClass())) {
			synchronized (subject) {
				modifyHealth(-Projectile.DAMAGE);
			}
		}
	}

	@Override
	public void physicalStep() {
		// Check here, as if things are initialised late, can cause
		// a problem
		if (getCurrentTile() != null) {
			// Check that the tile rigid body is active, if not,
			// suspend the model
			if (getCurrentTile().getRigidBody() == null) {
				rigidBody.setActivationState(0);
			} else {
				rigidBody.setActivationState(1);
				rigidBody.activate();

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
		}
	}

	public void modifyHealth(int modification) {
		health += modification;
	}

	@Override
	public boolean isDestroyable() {

		if (health <= 0) {
			AudioEngine.getInstance().playMonsterSound(getPosition());
			destroyable = true;
		}

		// /if (destroyable) {
		// destroy();
		// }

		return destroyable;
	}

	public int getHealth() {
		return health;
	}
}
