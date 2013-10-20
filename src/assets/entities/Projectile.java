package assets.entities;

import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import assets.world.AbstractTile;
import assets.world.datastructures.TileDataStructure;
import collision.Collidable;

public class Projectile extends Entity {

	private static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;

	private int age;
	private float[] color;

	private Vector3f movementVector;

	public Projectile(GLModel model, float[] color, Vector3f movementVector) {
		super(model, "Projectile " + System.currentTimeMillis());
		this.age = 0;
		this.color = color;

		this.movementVector = movementVector;
	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	public void move(int timeDelta) {
		model.modelPos.x += movementVector.x * DEFAULT_MOVEMENT_SPEED * timeDelta;
		model.modelPos.y += movementVector.y * DEFAULT_MOVEMENT_SPEED * timeDelta;
	}

	@Override
	public void collidedWith(Collidable subject) {
		// DAVE: Isn't this just a neater way of saying
		// Monster.class.isAssignableFrom(subject.getClass())?
		if (Monster.class.isInstance(subject)) {
			// health--;
			// System.out.printf("Health: %d\n", health);

			// if (health < 1) {
			// GameControl.playerDead();
			// }

			System.out.println("Hit");
		}
	}

	@Override
	public void locatedWithin(AbstractTile tile, TileDataStructure data) {
		if (tile != currentTile) {
			currentTile = tile;
			System.out.println(tile.getKey().x + "," + tile.getKey().y);
			data.populateNeighbouringTiles(currentTile);
		}
	}

}
