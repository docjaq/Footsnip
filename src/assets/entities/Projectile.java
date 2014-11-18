package assets.entities;

import math.types.Matrix4;
import math.types.Vector3;
import math.types.Vector4;
import renderer.GLPosition;
import renderer.GLWorld;
import renderer.glmodels.GLModel;
import collision.Collidable;

public class Projectile extends Entity {

	private static final int DAMAGE = 10;

	public static final float DEFAULT_MOVEMENT_SPEED = 0.00001f;
	public static final float ADDITIVE_VELOCITY_SCALE = 50.00f;

	public static final long MAXIMUM_AGE = 1000;

	private long spawnTime;
	private float[] color;

	private Vector3 movementVector;

	private boolean hasFired;

	public Projectile(GLModel model, GLPosition position, Vector3 movementVector) {
		super(model, position);

		this.position.setEntityRadiusWithModelRadius(this.model.getModelRadius());
		this.spawnTime = System.currentTimeMillis();

		this.movementVector = movementVector;

		Vector4 additiveMovement = new Vector4(ADDITIVE_VELOCITY_SCALE, 0.0f, 0.0f, 1.0f);

		Matrix4 rotationMatrix = new Matrix4().clearToIdentity();
		rotationMatrix.rotateDeg(position.modelAngle.z(), GLWorld.BASIS_Z);
		rotationMatrix.rotateDeg(position.modelAngle.y(), GLWorld.BASIS_Y);
		rotationMatrix.rotateDeg(position.modelAngle.x(), GLWorld.BASIS_X);

		additiveMovement = rotationMatrix.mult(additiveMovement);

		Vector3 vec3fAdditiveMovement = new Vector3(additiveMovement.x(), additiveMovement.y(), additiveMovement.z());

		movementVector.add(vec3fAdditiveMovement);

		// System.out.println("Position = (" + position.modelPos.x() + "," +
		// position.modelPos.y() + "," + position.modelPos.z());
		// System.out.println("AdditiveMovement = (" + vec3fAdditiveMovement.x()
		// + "," + vec3fAdditiveMovement.y() + ","
		// + vec3fAdditiveMovement.z());
		position.modelPos.z(position.modelPos.z() - 0.01f);
		position.modelPos.add(vec3fAdditiveMovement.normalize().mult(0.03f));

		setChanged();

	}

	public long getAge() {
		return System.currentTimeMillis() - spawnTime;
	}

	public float[] getColor() {
		return color;
	}

	// public void move(int timeDelta) {
	// if (model != null) {
	// position.modelPos.x(position.modelPos.x() + movementVector.x() *
	// DEFAULT_MOVEMENT_SPEED * timeDelta);
	// position.modelPos.y(position.modelPos.y() + movementVector.y() *
	// DEFAULT_MOVEMENT_SPEED * timeDelta);
	// }
	// }

	// public void createModel(GLProjectileFactory projectileFactory) {
	// if (this.model != null) {
	// throw new RuntimeException("You can only set the model once.");
	// }
	// float[] color = { 0.0f, 0.4f, 1.0f, 1.0f };
	//
	// this.model = projectileFactory.create(color);
	// this.position.setEntityRadiusWithModelRadius(this.model.getModelRadius());
	// }

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

	public Vector3 getMovementVector() {
		return movementVector;
	}

	public boolean getHasFired() {
		return hasFired;
	}

	public void setHasFired(boolean hasFired) {
		this.hasFired = hasFired;
	}
}
