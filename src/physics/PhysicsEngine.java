package physics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import renderer.glmodels.GLMesh;
import assets.AssetContainer;
import assets.entities.Monster;
import assets.world.AbstractTile;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.SphereShape;
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

public class PhysicsEngine implements Observer {

	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private CollisionDispatcher dispatcher;
	private BroadphaseInterface broadphase;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;

	private DynamicsWorld dynamicsWorld = null;
	private AssetContainer assContainer;

	protected Clock clock = new Clock();
	private static float offset = 0f;

	private Transform initialTransform;

	// private TriangleIndexVertexArray terrainMeshArray;

	// TODO: Change all of this from monster to entity
	private Map<RigidBody, Monster> objectMap;

	public void stepSimulation() {

		float dt = getDeltaTimeMicroseconds() * 0.000001f;

		long t0 = System.nanoTime();

		offset += 0.01f;

		dynamicsWorld.stepSimulation(dt);
		dynamicsWorld.debugDrawWorld();

		// System.out.println(BulletStats.gNumGjkChecks + "," +
		// BulletStats.gNumDeepPenetrationChecks);
		// System.out.println("Updating physics engine");

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

			// If that entity is contained within the map
			if (objectMap.containsKey(body)) {
				Monster monster = objectMap.get(body);

				// Check that the tile rigid body is active, if not, suspend
				// the model
				if (monster.getCurrentTile().getRigidBody() == null) {
					body.setActivationState(0);
				} else {
					if (body.getActivationState() == 0) {
						body.setActivationState(1);
						body.applyImpulse(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
					}

					if (body != null && body.getMotionState() != null) {
						DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
						m.set(myMotionState.graphicsWorldTrans);

					} else {
						colObj.getWorldTransform(m);
					}

					// Update its rendering position
					monster.getPosition().setModelPos(new Vector3(m.origin.x, m.origin.y, m.origin.z));
				}
			}
		}

		// System.out.println(count);

	}

	private IndexedMesh addTerrainMesh(GLMesh mesh) {

		/*
		 * public int numTriangles; public ByteBuffer triangleIndexBase; public
		 * int triangleIndexStride; public int numVertices; public ByteBuffer
		 * vertexBase; public int vertexStride;
		 */
		IndexedMesh indexedMesh = new IndexedMesh();
		indexedMesh.numTriangles = mesh.numTriangles;
		indexedMesh.triangleIndexBase = mesh.indexByteBuffer;
		indexedMesh.triangleIndexStride = mesh.indexStride;

		indexedMesh.numVertices = mesh.numVertices;
		indexedMesh.vertexBase = mesh.verticesByteBuffer;
		indexedMesh.vertexStride = mesh.vertexStride;

		return indexedMesh;
	}

	public PhysicsEngine(AssetContainer assContainer) {

		this.assContainer = assContainer;

		// terrainMeshArray = new TriangleIndexVertexArray();
		// for (AbstractTile tile :
		// assContainer.getTileDataStructure().getTilesAsList()) {
		// if (tile.getModel() != null) {
		// addTerrainMesh((GLMesh) tile.getModel());
		// }
		// }

		// terrainMeshArray = new TriangleIndexVertexArray();

		System.out.println("Instantiating physics engine");

		// Some sort of global callback state
		BulletGlobals.setContactAddedCallback(new CustomMaterialCombinerCallback());

		// Init mesh
		// Create TriangleIndexVertexArray object.
		// Do I have to make one, or can I just implement an interface?
		// TestGeometryCreation testGeometry = new TestGeometryCreation();
		// TriangleIndexVertexArray indexVertexArrays = new
		// TriangleIndexVertexArray(testGeometry.totalTriangles,
		// testGeometry.gIndices,
		// testGeometry.indexStride, testGeometry.totalVerts,
		// testGeometry.gVertices, testGeometry.vertStride);

		// Look at what BvhTriangleMeshShape is. Maybe I can pass it something
		// else, if it's implemented
		// TEMP boolean useQuantizedAabbCompression = true;

		// TEMP trimeshShape = new BvhTriangleMeshShape(terrainMeshArray,
		// useQuantizedAabbCompression);
		// TEMP collisionShapes.add(trimeshShape);

		// Store a reference to the terrain collision shape
		// TEMP CollisionShape groundShape = trimeshShape;
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

		dynamicsWorld.setGravity(new Vector3f(0, 0, -1f));

		initialTransform = new Transform();
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

		objectMap = new HashMap<RigidBody, Monster>();

		// TEMP2 addAsCubes(initialTransform, assContainer.getMonsters());

		// Add the static terrain to the dynamicsWorld. Allow material
		// callbacks? Friction etc
		initialTransform.setIdentity();
		// TEMP RigidBody staticBody = localCreateRigidBody(mass,
		// initialTransform, groundShape);
		// TEMP staticBody.setCollisionFlags(staticBody.getCollisionFlags() |
		// CollisionFlags.STATIC_OBJECT);
		// TEMP staticBody.setCollisionFlags(staticBody.getCollisionFlags() |
		// CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
	}

	private void addAsCubes(Transform transform, List<Monster> entities) {

		int count = 0;
		for (Monster e : entities) {
			transform.setIdentity();
			// TODO: Radius hack as it seems too big
			CollisionShape colShape = new SphereShape(e.getModel().getModelRadius() * 0.8f);

			transform.origin.set(e.getPosition().modelPos.x(), e.getPosition().modelPos.y(), e.getPosition().modelPos.z() + 0.2f);
			RigidBody body = localCreateRigidBody(1f, transform, colShape);
			body.setUserPointer(e);

			objectMap.put(body, e);

			// dynamicsWorld.addRigidBody(body);
			count++;
		}
		System.out.println("Added " + count + " entities to Physics Engine");
	}

	private void addAbstractTile(Transform transform, AbstractTile tile) {

		GLMesh model = (GLMesh) tile.getPhysicsModel();

		TriangleIndexVertexArray terrainMeshArray = new TriangleIndexVertexArray();

		terrainMeshArray.addIndexedMesh(addTerrainMesh(model));

		boolean useQuantizedAabbCompression = true;

		BvhTriangleMeshShape trimeshShape = new BvhTriangleMeshShape(terrainMeshArray, useQuantizedAabbCompression);
		// collisionShapes.add(trimeshShape);

		// CollisionShape groundShape = trimeshShape;

		transform.origin.set(tile.getPosition().modelPos.x(), tile.getPosition().modelPos.y(), tile.getPosition().modelPos.z());

		float mass = 0f;
		RigidBody staticBody = localCreateRigidBody(mass, transform, trimeshShape);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);

		tile.setRigidBody(staticBody);
	}

	// TODO: Not sure this is all correct, and not sure I defo want to throw
	// away the reference.
	private void removeAbstractTile(AbstractTile tile) {
		dynamicsWorld.removeRigidBody(tile.getRigidBody());
		tile.setRigidBody(null);
	}

	private void addAsCube(Transform transform, Monster entity) {

		int count = 0;

		transform.setIdentity();
		// TODO: Radius hack as it seems too big
		CollisionShape colShape = new SphereShape(entity.getModel().getModelRadius() * 0.8f);

		transform.origin
				.set(entity.getPosition().modelPos.x(), entity.getPosition().modelPos.y(), entity.getPosition().modelPos.z() + 0.2f);
		RigidBody body = localCreateRigidBody(1f, transform, colShape);
		body.setUserPointer(entity);

		objectMap.put(body, entity);

		// dynamicsWorld.addRigidBody(body);
		count++;
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

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Monster) {
			initialTransform.setIdentity();
			addAsCube(initialTransform, (Monster) arg0);

		}

		if (arg0 instanceof AbstractTile) {
			if (((AbstractTile) arg0).getPhysicsModel() == null) {
				System.out.println("Removing tile from physics engine");
				removeAbstractTile((AbstractTile) arg0);
			} else {
				System.out.println("Adding tile to physics engine");
				initialTransform.setIdentity();
				addAbstractTile(initialTransform, (AbstractTile) arg0);
			}
		}

	}
}
