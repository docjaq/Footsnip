package assets.entities;

import location.Locatable;
import math.LinearAlgebra;
import renderer.GLPosition;
import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;
import collision.Collidable;

public class Entity extends AbstractEntity implements Collidable, Locatable {

	private String name;
	protected AbstractTile currentTile;
	protected boolean destroyable = false;

	public Entity(GLModel model, GLPosition position, String name) {
		super(model, position);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getEuclideanDistance(Entity other) {
		return LinearAlgebra.euclideanDistance(this.position.modelPos, other.position.modelPos);
	}

	@Override
	public void collidedWith(Collidable subject) {
		System.out.println(this.toString() + " collided with " + subject.toString());
	}

	@Override
	public boolean readyForCollisionDetection() {
		return model != null;
	}

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
				destroyable = true;
			}
		}
	}

	public boolean isDestroyable() {
		// if (destroyable) {
		// destroy();
		// }
		return destroyable;
	}

	public void destroy() {
		currentTile.getContainedEntities().remove(this);
	}

	public AbstractTile getCurrentTile() {
		return currentTile;
	}
}
