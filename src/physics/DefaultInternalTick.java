package physics;

import javax.vecmath.Vector3f;

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
			Entity physicsObject1 = (Entity) object1.getUserPointer();
			Entity physicsObject2 = (Entity) object2.getUserPointer();
			boolean hit = false;
			Vector3f normal = null;
			for (int j = 0; j < manifold.getNumContacts(); j++) {
				ManifoldPoint contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f) {
					hit = true;
					normal = contactPoint.normalWorldOnB;
					break;
				}
			}
			if (hit) {
				// Collision happened between physicsObject1 and
				// physicsObject2. Collision normal is in variable
				// 'normal'.
				System.err.println("DefaultInternalTick HIT");
			}
		}

	}
}
