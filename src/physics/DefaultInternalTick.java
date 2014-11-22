package physics;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import assets.entities.Entity;

import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;

public class DefaultInternalTick extends InternalTickCallback {

	@Override
	public void internalTick(DynamicsWorld world, float timeStep) {

		Dispatcher dispatcher = world.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();
		for (int i = 0; i < manifoldCount; i++) {
			PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
			// The following two lines are optional.
			RigidBody object1 = (RigidBody) manifold.getBody0();
			RigidBody object2 = (RigidBody) manifold.getBody1();
			Entity entity0 = (Entity) object1.getUserPointer();
			Entity entity1 = (Entity) object2.getUserPointer();
			boolean hasCollided = false;
			Vector3 collisionNormal = null;
			for (int j = 0; j < manifold.getNumContacts(); j++) {
				ManifoldPoint contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f) {
					hasCollided = true;
					Vector3f normalf = contactPoint.normalWorldOnB;
					collisionNormal = new Vector3(normalf.x, normalf.y, normalf.z);
					break;
				}
			}
			if (hasCollided) {
				// TODO: Should it be done in both directions? I.e. entity0
				// affects itself based on entity1, then entity1 effects itself
				// based on entity0?

				if (entity0 != null && entity1 != null) {
					entity0.collidedWith(entity1, collisionNormal);
					entity1.collidedWith(entity0, collisionNormal);
				} else {
					// TODO: This happens if there is a terrain collision!
					// Handle this. Probably better to check casts above!
				}
			}
		}

	}
}
