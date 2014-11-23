package physics;

import com.bulletphysics.collision.dispatch.CollisionObject;

public interface Physical {

	public void physicalStep(CollisionObject collisionObject);
}
