package assets.entities;

import static maths.LinearAlgebra.degreesToRadians;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.GLWorld;
import renderer.glmodels.GLProjectileFactory;
import collision.Collidable;

public class Projectile extends Entity {

	private static final int DAMAGE = 10;

	private static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	private static final float ADDITIVE_VELOCITY_SCALE = 50.00f;

	private int age;
	private float[] color;

	private Vector3f movementVector;
	private Vector3f startPosition;
	private Vector3f angle;
	private float scale;

	public Projectile(Vector3f startPosition, Vector3f angle, float scale, Vector3f movementVector) {
		super(null, "Projectile " + System.currentTimeMillis());
		this.age = 0;

		this.startPosition = startPosition;
		this.startPosition.z -= 0.01;
		this.angle = angle;
		this.scale = scale;

		this.movementVector = movementVector;
		// System.out.println(angle.x + "," + angle.y + "," + angle.z);

		// TODO: This could all be done a bit more neatly
		Vector4f additiveMovement = new Vector4f(ADDITIVE_VELOCITY_SCALE, 0.0f, 0.0f, 1.0f);

		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.rotate(degreesToRadians(angle.z), GLWorld.BASIS_Z);
		rotationMatrix.rotate(degreesToRadians(angle.y), GLWorld.BASIS_Y);
		rotationMatrix.rotate(degreesToRadians(angle.x), GLWorld.BASIS_X);

		Matrix4f.transform(rotationMatrix, additiveMovement, additiveMovement);
		Vector3f vec3fAdditiveMovement = new Vector3f(additiveMovement.x, additiveMovement.y, additiveMovement.z);

		Vector3f.add(this.movementVector, vec3fAdditiveMovement, this.movementVector);

	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	public void move(int timeDelta) {
		if (model != null) {
			model.modelPos.x += movementVector.x * DEFAULT_MOVEMENT_SPEED * timeDelta;
			model.modelPos.y += movementVector.y * DEFAULT_MOVEMENT_SPEED * timeDelta;
		}
	}

	public void createModel(GLProjectileFactory projectileFactory) {
		if (this.model != null) {
			throw new RuntimeException("You can only set the model once.");
		}
		float[] color = { 0.0f, 0.4f, 1.0f, 1.0f };

		this.model = projectileFactory.create(this.startPosition, this.angle, this.scale, color);

	}

	@Override
	public void collidedWith(Collidable subject) {
		if (!destroyable) {
			// Lock the subject so that multiple fast collisions (faster than
			// the rendering thread) don't cause monster health to be reduced
			// too often
			synchronized (subject) {
				// If it hits a monster
				if (Monster.class.isAssignableFrom(subject.getClass())) {
					System.out.println("Hit");
					((Monster) subject).modifyHealth(-DAMAGE);
				}

				// If it hits a monster
				// if (!Entity.class.isAssignableFrom(subject.getClass())) {
				if (Monster.class.isAssignableFrom(subject.getClass())) {
					// System.out.println("Is this happening?");
					destroyable = true;
				}
			}

		}
	}

	@Override
	public boolean isDestroyable() {
		return destroyable;
	}
}
