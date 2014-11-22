package collision;

import math.types.Vector3;

public interface Collidable {
	public void collidedWith(final Collidable subject, final Vector3 collisionNormal);
}
