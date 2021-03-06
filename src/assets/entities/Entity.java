package assets.entities;

import location.Locatable;
import math.LinearAlgebra;
import math.types.Vector3;
import physics.Physical;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;
import collision.Collidable;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Entity extends AbstractEntity implements Collidable, Locatable, Physical {

	protected AbstractTile currentTile;
	protected boolean destroyable = false;
	protected RigidBody rigidBody;

    protected Transform physicsTransform;
	protected float mass;

	public Entity(GLModel model, GLPosition position, float mass) {
		super(model, position);
		physicsTransform = new Transform();
		this.mass = mass;
	}

	public float getEuclideanDistance(Entity other) {
		return LinearAlgebra.euclideanDistance(this.position.modelPos, other.position.modelPos);
	}

    //Callback from datastructure
	@Override
	public void locatedWithin(AbstractTile tile, TileDataStructure2D data) {

		if (tile != currentTile) {
			if (currentTile != null) {
				currentTile.getContainedEntities().remove(this);
			}
			if (tile != null) {
				tile.getContainedEntities().add(this);
				currentTile = tile;
			} else {
				// If the tile doesn't exist at all, for now, remove the entity
				// if (this instanceof Monster) {
				// System.err.println("Current tile is a monster!");
				// System.err.println("\tcurrent tile is " + currentTile);
				// }
				// if (this instanceof Asteroid) {
				// System.err.println("Current tile is an asteroid!");
				// System.err.println("\tcurrent tile is " + currentTile);
				// }
				destroyable = true;
			}
		}
	}

    //Callback from physics engine interface
    @Override
    public void collidedWith(final Collidable subject, final Vector3 collisionNormal) {
        System.out.println(this.toString() + " collided with " + subject.toString());
    }

    //Callback from the physics engine
	@Override
	public void physicalStep() {

		// Perform a basic physical step that just updates the model position to
		// that of the physics world

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

			// Update its rendering position
			getPosition().setModelPos(new Vector3(physicsTransform.origin.x, physicsTransform.origin.y, physicsTransform.origin.z));
		}
	}

	public boolean isDestroyable() {
		// if (destroyable) {
		// destroy();
		// }
		return destroyable;
	}

	public void destroy() {
		if (currentTile != null) {
			currentTile.getContainedEntities().remove(this);
		} else {
			System.err.println("Entity has no tile, cannot remove!");
		}
	}

	public AbstractTile getCurrentTile() {
		return currentTile;
	}

	public RigidBody getRigidBody() {
		return rigidBody;
	}

	public void setRigidBody(RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}

    public Transform getPhysicsTransform() {return physicsTransform;}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}
}
