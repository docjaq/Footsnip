package assets.entities;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.vecmath.Vector3f;

import math.types.Vector3;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import scripting.Script;
import scripting.ScriptFunction;
import scripts.MonsterScript;
import collision.Collidable;

import com.bulletphysics.linearmath.DefaultMotionState;

import java.io.FileNotFoundException;

public class Monster extends NonPlayer {

	public Monster(GLModel model, GLPosition position, float mass) {
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

//	@Override
//	public void physicalStep() {
//		// Check here, as if things are initialised late, can cause
//		// a problem
//		if (getCurrentTile() != null) {
//
//			Vector3 movementDirection = new Vector3();
//			for (Entity entity : getCurrentTile().getContainedEntities()) {
//				if (entity instanceof Player) {
//					movementDirection = MonsterScript.update(this, (Player) entity);
//					movementDirection.mult(0.01f);
//				}
//			}
//
//			// Check that the tile rigid body is active, if not,
//			// suspend the model
//			if (getCurrentTile().getRigidBody() == null) {
//				rigidBody.setActivationState(0);
//			} else {
//				rigidBody.setActivationState(1);
//				rigidBody.activate();
//
//				if (rigidBody != null && rigidBody.getMotionState() != null) {
//					DefaultMotionState myMotionState = (DefaultMotionState) rigidBody.getMotionState();
//					physicsTransform.set(myMotionState.graphicsWorldTrans);
//
//				} else {
//					rigidBody.getWorldTransform(physicsTransform);
//				}
//
//				rigidBody.applyCentralImpulse(new Vector3f(movementDirection.x(), movementDirection.y(), movementDirection.z()));
//				// Force the body to hover on a plane. May cause
//				// z-oscillations; I don't fucking know, I'm not a
//				// physicist.
//				rigidBody.applyCentralImpulse(new Vector3f(0, 0, 0 - physicsTransform.origin.z));
//
//				// Update its rendering position
//				getPosition().setModelPos(new Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
//			}
//		}
//	}

    @Override
    public void physicalStep() {



        if (getCurrentTile() != null) {

            //System.out.println(getCurrentTile().getContainedEntities().get(0).getClass());

            Script.MONSTER.runFunction(ScriptFunction.UPDATE, this);
        }
    }

	@Override
	public boolean isDestroyable() {

		if (health <= 0) {

			destroyable = true;
		}

		return destroyable;
	}
}
