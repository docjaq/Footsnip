package collision;

import java.util.List;

import assets.entities.Entity;

public class CollisionMethods {
	private static boolean sphericalIntersectionTest(Entity a, Entity b) {
		float distance = a.getEuclideanDistance(b);
		if (distance < a.getPosition().getEntityRadius() + b.getPosition().getEntityRadius()) {
			return true;
		}
		return false;
	}

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
