package assets.entities;

import location.Locatable;
import maths.LinearAlgebra;
import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure2D;
import collision.Collidable;

public class Entity extends AbstractEntity implements Collidable, Locatable {

	private String name;
	protected AbstractTile currentTile;

	public Entity(GLModel model, String name) {
		super(model);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getEuclideanDistance(Entity other) {
		return LinearAlgebra.euclideanDistance(this.model.modelPos, other.model.modelPos);
	}

	@Override
	public void collidedWith(Collidable subject) {
		System.out.println(this.toString() + " collided with " + subject.toString());
	}

	@Override
	public boolean readyForCollisionDetection() {
		return model != null;
	}

	@Override
	public void locatedWithin(AbstractTile tile, TileDataStructure2D data) {
		if (tile != currentTile) {
			currentTile = tile;
		}
	}
}
