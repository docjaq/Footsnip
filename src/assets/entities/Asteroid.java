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

				// btTransform tr;
				// tr.setIdentity();
				// btQuaternion quat;
				// quat.setEuler(yaw,pitch,roll); //or quat.setEulerZYX
				// depending on the ordering you want
				// tr.setRotation(quat);
				//
				// rigidBody->setCenterOfMassTransform(tr);

				// Force the body to hover on a plane. May cause z-oscillations
				rigidBody.applyCentralImpulse(new Vector3f(0, 0, 0 - physicsTransform.origin.z));

				// rigidBody.
				// rigidBody.applyForce(force, rel_pos);
				// rigidBody.applyTorque(new Vector3f(20.f, 100.0f, 0.0f));

				// Quat4f quat = new Quat4f();
				// float twoPi = (float) (Math.PI * 2);
				// QuaternionUtil.setEuler(quat, (float) Math.random() * twoPi,
				// (float) Math.random() * twoPi, (float) Math.random() *
				// twoPi);
				// physicsTransform.setRotation(quat);
				// rigidBody.setCenterOfMassTransform(physicsTransform);

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
