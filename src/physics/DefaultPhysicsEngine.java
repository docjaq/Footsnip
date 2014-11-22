package physics;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.vecmath.Vector3f;

import math.types.Vector3;
import renderer.glmodels.GLMesh;
import assets.entities.Entity;
import assets.entities.Monster;
import assets.entities.Player;
import assets.entities.Projectile;
import assets.world.AbstractTile;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class DefaultPhysicsEngine extends PhysicsEngine implements Observer {

	private static final float STEP_ADJUSTMENT_FACTOR = 0.000001f;

	private Transform initialTransform;

	private Queue<AbstractTile> tilesToRemove;
	private Queue<AbstractTile> tilesToAdd;
	private Queue<Entity> entitiesToRemove;
	private Queue<Entity> entitiesToAdd;

	// private Map<RigidBody, Entity> objectMap;
	private Player playerEntity;

	private int debugTerrainAddedCount = 0;

	// TODO: Look into different settings for initialisation of engine
	public DefaultPhysicsEngine() {
		super();

		System.out.println("Instantiating physics engine");

		// Some sort of global callback state
		BulletGlobals.setContactAddedCallback(new CustomMaterialCombinerCallback());
		BulletGlobals.setContactProcessedCallback(new DefaultContactProcessedCallback());

		collisionConfiguration = new DefaultCollisionConfiguration();

		// Init dispatcher
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		// Create bounding box for physics world. Some options here.
		broadphase = new DbvtBroadphase();

		// Create solver and world. Can possibly set a property on the
		// dynamicsWorld to enable parallelisation
		solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0, 0, -0.1f));

		dynamicsWorld.setInternalTickCallback(new DefaultInternalTick(), null);

		initialTransform = new Transform();

		// objectMap = new HashMap<RigidBody, Entity>();

		// Add the static terrain to the dynamicsWorld. Allow material
		// callbacks? Friction etc
		initialTransform.setIdentity();

		tilesToRemove = new ArrayBlockingQueue<AbstractTile>(20);
		tilesToAdd = new ArrayBlockingQueue<AbstractTile>(20);

		entitiesToRemove = new ArrayBlockingQueue<Entity>(20);
		// Needs to be large because of the init. Could probably have a better
		// solution to this.
		entitiesToAdd = new ArrayBlockingQueue<Entity>(1000);
	}

	public void stepSimulation() {

		cleanUpRigidBodies();

		float dt = getDeltaTimeMicroseconds() * STEP_ADJUSTMENT_FACTOR;
		// long t0 = System.nanoTime();
		// offset += 0.01f;

		dynamicsWorld.stepSimulation(dt);

		updateEntities(dt);
	}

	private void cleanUpRigidBodies() {

		while (!tilesToRemove.isEmpty()) {
			removeAbstractTile(tilesToRemove.remove());
		}

		while (!tilesToAdd.isEmpty()) {
			addAbstractTile(tilesToAdd.remove());
		}

		while (!entitiesToRemove.isEmpty()) {
			removeEntity(entitiesToRemove.remove());
		}

		while (!entitiesToAdd.isEmpty()) {
			addEntity(entitiesToAdd.remove());
		}

	}

	protected void updateEntities(float deltaTime) {
		final Transform m = new Transform();

		for (int i = 0; i < dynamicsWorld.getNumCollisionObjects(); i++) {

			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);

			RigidBody body = RigidBody.upcast(colObj);

			Entity entity = (Entity) body.getUserPointer();
			// If that entity is contained within the map
			if (entity != null) {

				if (entity instanceof Monster) {
					Monster monster = (Monster) entity;

					// Check here, as if things are initialised late, can cause
					// a problem
					if (monster.getCurrentTile() != null) {

						// Check that the tile rigid body is active, if not,
						// suspend
						// the model
						if (monster.getCurrentTile().getRigidBody() == null) {
							body.setActivationState(0);
						} else {
							if (body.getActivationState() == 0) {

								body.setActivationState(1);

							}
							body.activate();
							// body.applyCentralForce(new Vector3f(0, 0, (float)
							// (0.100075)));

							if (body != null && body.getMotionState() != null) {
								DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
								m.set(myMotionState.graphicsWorldTrans);

							} else {
								colObj.getWorldTransform(m);
							}

							// Force the body to hover on a plane. May cause
							// z-oscillations; I don't fucking know, I'm not a
							// physicist.
							body.applyCentralImpulse(new Vector3f(0, 0, 0 - m.origin.z));

							// Update its rendering position
							monster.getPosition().setModelPos(new Vector3(m.origin.x, m.origin.y, m.origin.z));
						}
					}
				} else if (entity instanceof Player) {
					Player player = (Player) entity;

					if (body.getActivationState() == 0) {

						body.setActivationState(1);
					}
					body.activate();

					// TODO: Clean this up so A) it uses a pool not a queue, and
					// B) don't have to fucking convert vector formats
					Vector3f velocity = new Vector3f();
					while (!player.getControlInputMovement().isEmpty()) {
						Vector3 vec = player.getControlInputMovement().remove();
						velocity.x += vec.x() * 4;
						velocity.y += vec.y() * 4;
						velocity.z += vec.z() * 4;
					}

					// Limit maximum speed
					body.applyCentralForce(velocity);
					body.getLinearVelocity(velocity);
					float speed = velocity.length();
					if (speed > Player.MAX_MOVEMENT_SPEED) {
						velocity.scale(Player.MAX_MOVEMENT_SPEED / speed);
						body.setLinearVelocity(velocity);
					}

					if (body != null && body.getMotionState() != null) {
						DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
						m.set(myMotionState.graphicsWorldTrans);
					} else {
						colObj.getWorldTransform(m);
					}

					// Force the body to hover on a plane. May cause
					// z-oscillations; I don't fucking know, I'm not a
					// physicist.
					body.applyCentralImpulse(new Vector3f(0, 0, 0 - m.origin.z));

					// Update its rendering position
					player.getPosition().setModelPos(new Vector3(m.origin.x, m.origin.y, m.origin.z));
				} else if (entity instanceof Projectile) {
					Projectile projectile = (Projectile) entity;

					if (projectile.getAge() > Projectile.MAXIMUM_AGE) {
						entitiesToRemove.add(projectile);
					} else {
						if (body.getActivationState() == 0) {
							body.setActivationState(1);
						}
						body.activate();

						if (body != null && body.getMotionState() != null) {
							DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
							m.set(myMotionState.graphicsWorldTrans);
						} else {
							colObj.getWorldTransform(m);
						}

						if (!projectile.getHasFired()) {
							Vector3 force = ((Projectile) entity).getMovementVector();
							Vector3f impulse = new Vector3f(force.x(), force.y(), force.z());
							Vector3f playerVelocity = new Vector3f();
							playerEntity.getRigidBody().getLinearVelocity(playerVelocity);
							impulse.scale(0.01f);
							impulse.add(playerVelocity);
							body.applyCentralImpulse(impulse);
							projectile.setHasFired(true);
						}

						// Force the body to hover on a plane. May cause
						// z-oscillations; I don't fucking know, I'm not a
						// physicist.
						body.applyCentralImpulse(new Vector3f(0, 0, 0 - m.origin.z));

						// Update its rendering position
						projectile.getPosition().setModelPos(new Vector3(m.origin.x, m.origin.y, m.origin.z));
					}
				}

			}
		}
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

	private void addAbstractTile(AbstractTile tile) {

		GLMesh model = (GLMesh) tile.getPhysicsModel();

		// Shouldn't be, but will stop an error if the physics mesh is thrown
		// away faster than we add it to the physics engine (I think).
		if (model == null) {
			return;
		}

		TriangleIndexVertexArray terrainMeshArray = new TriangleIndexVertexArray();
		// Create the physics mesh object from the buffers and strides in the
		// GLMesh
		terrainMeshArray.addIndexedMesh(addTerrainMesh(model));

		boolean useQuantizedAabbCompression = true;

		// TODO: I probably have to delete this too?
		BvhTriangleMeshShape trimeshShape = new BvhTriangleMeshShape(terrainMeshArray, useQuantizedAabbCompression);

		// CollisionShape groundShape = trimeshShape;
		Transform transform = new Transform();// .setIdentity();
		transform.setIdentity();
		transform.origin.set(tile.getPosition().modelPos.x(), tile.getPosition().modelPos.y(), tile.getPosition().modelPos.z());

		float mass = 0f;
		RigidBody staticBody = localCreateRigidBody(mass, transform, trimeshShape);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.STATIC_OBJECT);
		staticBody.setCollisionFlags(staticBody.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);

		tile.setRigidBody(staticBody);

		debugTerrainAddedCount++;
		System.out.printf("Added %d tiles\n", debugTerrainAddedCount);
	}

	// TODO: Some unknowns here in terms of what needs to be deleted. Consider
	// BvhTriangleMeshShame from before
	private void removeAbstractTile(AbstractTile tile) {
		removeRigidBody(tile.getRigidBody());
		tile.setRigidBody(null);
	}

	private void addEntity(Entity entity) {
		initialTransform.setIdentity();
		RigidBody body = addRigidBodyAsSphere(initialTransform, entity);
		entity.setRigidBody(body);
		body.setActivationState(1);
		body.setCollisionFlags(body.getCollisionFlags() | CollisionFlags.CUSTOM_MATERIAL_CALLBACK);
		// objectMap.put(body, entity);
		body.setUserPointer(entity);
	}

	private void removeEntity(Entity entity) {
		removeRigidBody(entity.getRigidBody());
		// objectMap.remove(entity.getRigidBody());
		entity.destroy();
	}

	private RigidBody addRigidBodyAsSphere(Transform transform, Entity entity) {

		transform.setIdentity();
		CollisionShape colShape = new SphereShape(entity.getModel().getModelRadius() * 1f);

		transform.origin.set(entity.getPosition().modelPos.x(), entity.getPosition().modelPos.y(), entity.getPosition().modelPos.z());
		RigidBody body = localCreateRigidBody(1f, transform, colShape);

		// TODO: See if this user point enables a neater solution than the
		// hashmap
		body.setUserPointer(entity);

		// TODO: Investigate and tweak these settings
		body.setDamping(0.1f, 0.1f);
		body.setRestitution(0.1f);

		return body;
	}

	public RigidBody localCreateRigidBody(float mass, Transform startTransform, CollisionShape shape) {
		// Rigidbody is dynamic if and only if mass is non zero, otherwise
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

		// dynamicsWorld.addRigidBody(body);
		dynamicsWorld.addRigidBody(body);
		// dynamicsWorld.addCo

		return body;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Entity) {
			entitiesToAdd.add((Entity) arg0);

			if (arg0 instanceof Player) {
				playerEntity = (Player) arg0;
			}
		}

		if (arg0 instanceof AbstractTile) {
			if (((AbstractTile) arg0).getPhysicsModel() == null) {
				tilesToRemove.add((AbstractTile) arg0);
			} else {
				tilesToAdd.add((AbstractTile) arg0);
			}
		}

	}

	/*
	 * public void finishedInitialisation() { this.finishedInitialisation =
	 * true; }
	 */
}
