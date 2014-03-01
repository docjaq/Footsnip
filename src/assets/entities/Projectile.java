package assets.entities;

import maths.types.Matrix4;
import maths.types.Vector3;
import maths.types.Vector4;
import renderer.GLWorld;
import renderer.glmodels.GLProjectileFactory;
import collision.Collidable;

public class Projectile extends Entity {

	private static final int DAMAGE = 10;

	private static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	private static final float ADDITIVE_VELOCITY_SCALE = 50.00f;

	private int age;
	private float[] color;

	private Vector3 movementVector;
	private Vector3 startPosition;
	private Vector3 angle;
	private float scale;

	public Projectile(Vector3 startPosition, Vector3 angle, float scale, Vector3 movementVector) {
		super(null, "Projectile " + System.currentTimeMillis());
		this.age = 0;

		this.startPosition = startPosition;
		this.startPosition.x(this.startPosition.x() - 0.01f);
		this.angle = angle;
		this.scale = scale;

		this.movementVector = movementVector;
		// System.out.println(angle.x + "," + angle.y + "," + angle.z);

		// TODO: This could all be done a bit more neatly
		Vector4 additiveMovement = new Vector4(ADDITIVE_VELOCITY_SCALE, 0.0f, 0.0f, 1.0f);
		// System.out.println("addMov = " + additiveMovement.x() + ", " +
		// additiveMovement.y() + ", " + additiveMovement.z());
		// System.out.println("angle = " + angle.x() + ", " + angle.y() + ", " +
		// angle.z());

		Matrix4 rotationMatrix = new Matrix4().clearToIdentity();
		rotationMatrix.rotateDeg(angle.z(), GLWorld.BASIS_Z);
		rotationMatrix.rotateDeg(angle.y(), GLWorld.BASIS_Y);
		rotationMatrix.rotateDeg(angle.x(), GLWorld.BASIS_X);

		// Matrix4f.transform(rotationMatrix, additiveMovement,
		// additiveMovement);
		// System.out.println(rotationMatrix.);

		additiveMovement = rotationMatrix.mult(additiveMovement);

		Vector3 vec3fAdditiveMovement = new Vector3(additiveMovement.x(), additiveMovement.y(), additiveMovement.z());

		// Vector3.add(this.movementVector, vec3fAdditiveMovement,
		// this.movementVector);
		movementVector.add(vec3fAdditiveMovement);

	}

	public int getAge() {
		return age;
	}

	public float[] getColor() {
		return color;
	}

	public void move(int timeDelta) {
		if (model != null) {
			model.modelPos.x(model.modelPos.x() + movementVector.x() * DEFAULT_MOVEMENT_SPEED * timeDelta);
			model.modelPos.y(model.modelPos.y() + movementVector.y() * DEFAULT_MOVEMENT_SPEED * timeDelta);
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
}
