package physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
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