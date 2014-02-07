package collision;

import java.util.List;

import assets.entities.Entity;

public class CollisionMethods {
	private static boolean sphericalIntersectionTest(Entity a, Entity b) {
		float distance = a.getEuclideanDistance(b);
		if (distance < a.getModel().getRadius() + b.getModel().getRadius()) {
			return true;
		}
		return false;
	}

	// TODO: This is a bit limited at the moment, as it assumes that we're
	// always testing against all entities
	public static void checkEntityCollisions(List<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				if (CollisionMethods.sphericalIntersectionTest(entities.get(i), entities.get(j))) {
					entities.get(i).collidedWith(entities.get(j));
					entities.get(j).collidedWith(entities.get(i));
				}
			}
		}
	}

}
