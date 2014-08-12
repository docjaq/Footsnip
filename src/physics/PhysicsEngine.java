package physics;

import java.util.List;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import assets.AssetContainer;
import assets.entities.Monster;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

public class PhysicsEngine {

	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private static BvhTriangleMeshShape trimeshShape;
	private CollisionDispatcher dispatcher;
	private BroadphaseInterface broadphase;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;

	private DynamicsWorld dynamicsWorld = null;
	private AssetContainer assContainer;

	protected Clock clock = new Clock();
	private static float offset = 0f;

	public void stepSimulation() {

		float dt = getDeltaTimeMicroseconds() * 0.000001f;

		long t0 = System.nanoTime();

		offset += 0.01f;

		dynamicsWorld.stepSimulation(dt);
		dynamicsWorld.debugDrawWorld();

		// System.out.println(BulletStats.gNumGjkChecks + "," +
		// BulletStats.gNumDeepPenetrationChecks);

		updateEntities();
	}

	private void updateEntities() {
		final Transform m = new Transform();
		// System.out.println("Num bodies = " +
		// dynamicsWorld.getNumCollisionObjects());

		int count = 0;

		for (int i = 0; i < dynamicsWorld.getNumCollisionObjects(); i++) {

			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);

			RigidBody body = RigidBody.upcast(colObj);

			if (body != null && body.getMotionState() != null) {
				DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
				m.set(myMotionState.graphicsWorldTrans);
			} else {
				colObj.getWorldTransform(m);
			}

			// Update object position
			if (body.getUserPointer() == assContainer.getMonsters().get(0)) {
				// System.out.println(m.origin);
				assContainer.getMonsters().get(0).getPosition().setModelPos(new Vector3(m.origin.x, m.origin.z, m.origin.y));
			}

		}

		System.out.println(count);

	}

	public PhysicsEngine(AssetContainer assContainer) {

		this.assContainer = assContainer;

		System.out.println("Instantiating physics engine");

		// Some sort of global callback state
		BulletGlobals.setContactAddedCallback(new CustomMaterialCombinerCallback());

		// Init mesh
		// Create TriangleIndexVertexArray object.
		// Do I have to make one, or can I just implement an interface?
		TestGeometryCreation testGeometry = new TestGeometryCreation();
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(testGeometry.totalTriangles, testGeometry.gIndices,
				testGeometry.indexStride, testGeometry.totalVerts, testGeometry.gVertices, testGeometry.vertStride);

		// Look at what BvhTriangleMeshShape is. Maybe I can pass it something
		// else, if it's implemented
		boolean useQuantizedAabbCompression = true;
		trimeshShape = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
		collisionShapes.add(trimeshShape);

		// Store a reference to the terrain collision shape
		CollisionShape groundShape = trimeshShape;
		collisionConfiguration = new DefaultCollisionConfiguration();

		// Init dispatcher
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		// Create bounding box for physics world. Some options here.
		broadphase = new DbvtBroadphase();
		// Vector3f worldMin = new Vector3f(-1000f, -1000f, -1000f);
		// Vector3f worldMax = new Vector3f(1000f, 1000f, 1000f);
		// broadphase = new AxisSweep3(worldMin, worldMax);
		// broadphase = new SimpleBroadphase();

		// Create solver and world. Can possibly set a property on the
		// dynamicsWorld to enable parallelisation
		solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		float mass = 0f;
		Transform initialTransform = new Transform();
		// Create some CollisionShape objects. If they're the same type of
		// object, just create one, and add it multiple times with different
		// transforms etc to the dynamics world. Example shape:
		/*
		 * CollisionShape colShape = new BoxShape(new Vector3f(1f, 1f, 1f));
		 * float mass = 0f; Transform initialTransform = new Transform();
		 * initialTransform.setIdentity(); initialTransform.origin.set(2f, 10f,
		 * 1f); RigidBody body = localCreateRigidBody(1f, initialTransform,
		 * colShape);
		 */
		// dynamicsWorld.addRigidBody(body);

		addAsCubes(initialTransform, assContainer.getMonsters());

		// Add the static terrain to the dynamicsWorld. Allow material
		// callbacks? Friction etc
		initialTransform.setIdentity();
		RigidBody staticBody = localCreateRigidBody(mass, initialTransform, groundShape);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
	}

	private void addAsCubes(Transform transform, List<Monster> entities) {

		int count = 0;
		for (Monster e : entities) {
			transform.setIdentity();
			CollisionShape colShape = new BoxShape(new Vector3f(1f, 1f, 1f));

			transform.origin.set(e.getPosition().modelPos.x(), e.getPosition().modelPos.y(), e.getPosition().modelPos.z());
			RigidBody body = localCreateRigidBody(1f, transform, colShape);
			body.setUserPointer(e);
			// dynamicsWorld.addRigidBody(body);
			count++;
		}
		System.out.println("Added " + count + " entities to Physics Engine");
	}

	public RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		// rigidbody is dynamic if and only if mass is non zero, otherwise
		// static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		if (isDynamic) {
			shape.calculateLocalInertia(mass, localInertia);
		}

		// using motionstate is recommended, it provides interpolation
		// capabilities, and only synchronizes 'active' objects

		// #define USE_MOTIONSTATE 1
		// #ifdef USE_MOTIONSTATE
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);

		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);

		RigidBody body = new RigidBody(cInfo);
		// #else
		// btRigidBody* body = new btRigidBody(mass,0,shape,localInertia);
		// body->setWorldTransform(startTransform);
		// #endif//

		dynamicsWorld.addRigidBody(body);

		return body;
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

	private static class CustomMaterialCombinerCallback extends ContactAddedCallback {
		public boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0, int partId0, int index0, CollisionObject colObj1,
				int partId1, int index1) {
			float friction0 = colObj0.getFriction();
			float friction1 = colObj1.getFriction();
			float restitution0 = colObj0.getRestitution();
			float restitution1 = colObj1.getRestitution();

			if ((colObj0.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				friction0 = 1f; // partId0,index0
				restitution0 = 0f;
			}
			if ((colObj1.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0) {
				if ((index1 & 1) != 0) {
					friction1 = 1f; // partId1,index1
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

	public float getDeltaTimeMicroseconds() {
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
	}
}
