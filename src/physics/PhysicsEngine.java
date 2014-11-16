package physics;

import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.linearmath.Clock;

public abstract class PhysicsEngine {
	protected CollisionDispatcher dispatcher;
	protected BroadphaseInterface broadphase;
	protected ConstraintSolver solver;
	protected DefaultCollisionConfiguration collisionConfiguration;
	protected DynamicsWorld dynamicsWorld = null;
	protected Clock clock;

	public PhysicsEngine() {
		clock = new Clock();
	}

	public abstract void stepSimulation();

	protected abstract void updateEntities(float deltaTime);

	protected float getDeltaTimeMicroseconds() {
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
	}

	protected static class CustomMaterialCombinerCallback extends ContactAddedCallback {
		public boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0, int partId0, int index0, CollisionObject colObj1,
				int partId1, int index1) {
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

			// this return value is currently ignored, but to be on the safe
			// side: return false if you don't calculate friction
			return true;
		}
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

	protected void removeRigidBody(RigidBody body) {
		if (body != null) {
			dynamicsWorld.removeRigidBody(body);

			if (body.isInWorld()) {
				System.err.println("RIGID BODY STILL EXISTS AFTER DELETION");
			}

			// dynamicsWorld.removeCollisionObject((CollisionObject)

			body.setMotionState(null);
			body.destroy();

		} else {
			System.err.println("Tile is null");
		}
	}
}