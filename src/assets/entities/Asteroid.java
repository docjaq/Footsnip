package assets.entities;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import audio.AudioEngine;
import collision.Collidable;

import com.bulletphysics.linearmath.DefaultMotionState;

public class Asteroid extends NonPlayer {

	public static final int DAMAGE = 2;

	public Asteroid(GLModel model, GLPosition position, float mass) {
		super(model, position, mass);
		setChanged();
	}

	@Override
	public void collidedWith(final Collidable subject, final Vector3 collisionNormal) {

		if (Asteroid.class.isAssignableFrom(subject.getClass())) {
			synchronized (subject) {
				modifyHealth(-Asteroid.DAMAGE);
			}
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

	@Override
	public boolean isDestroyable() {

		if (health <= 0) {
			AudioEngine.getInstance().playAsteroidSound(getPosition());
			destroyable = true;
		}

		return destroyable;
	}
}
