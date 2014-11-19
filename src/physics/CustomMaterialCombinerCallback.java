package physics;

import java.util.Map;

import assets.entities.Entity;

import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.RigidBody;

public class CustomMaterialCombinerCallback extends ContactAddedCallback {

	private Map<RigidBody, Entity> objectMap;

	public CustomMaterialCombinerCallback(Map<RigidBody, Entity> objectMap) {
		super();
		this.objectMap = objectMap;
	}

	public boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0, int partId0, int index0, CollisionObject colObj1, int partId1,
			int index1) {
		float friction0 = colObj0.getFriction();
		float friction1 = colObj1.getFriction();
		float restitution0 = colObj0.getRestitution();
		float restitution1 = colObj1.getRestitution();

		if ((colObj0.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
			friction0 = 0.2f; // partId0,index0
			restitution0 = 0f;
		}
		if ((colObj1.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
			if ((index1 & 1) != 0) {
				friction1 = 0.2f; // partId1,index1
			} else {
				friction1 = 0f;
			}
			restitution1 = 0f;
		}

		cp.combinedFriction = calculateCombinedFriction(friction0, friction1);
		cp.combinedRestitution = calculateCombinedRestitution(restitution0, restitution1);

		RigidBody rigidBody0 = RigidBody.upcast(colObj0);
		RigidBody rigidBody1 = RigidBody.upcast(colObj1);

		// TODO: Here, call some sort of method like collidedWith on both
		// objects, passing it the other object. Then sort out what we want to
		// do with them in that objects themselves. Clearly I then need to
		// update the physics engine. Needs some thought.

		System.out.println("Handling a rigid body collision!");

		// this return value is currently ignored, but to be on the safe
		// side: return false if you don't calculate friction
		return true;
	}

	/**
	 * User can override this material combiner by implementing
	 * gContactAddedCallback and setting body0->m_collisionFlags |=
	 * btCollisionObject::customMaterialCallback
	 */
	private static float calculateCombinedFriction(float friction0, float friction1) {
		float friction = friction0 * friction1;

		float MAX_FRICTION = 10f;
		if (friction < -MAX_FRICTION) {
			friction = -MAX_FRICTION;
		}
		if (friction > MAX_FRICTION) {
			friction = MAX_FRICTION;
		}
		return friction;
	}

	private static float calculateCombinedRestitution(float restitution0, float restitution1) {
		return restitution0 * restitution1;
	}
}
