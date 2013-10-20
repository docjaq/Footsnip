package collision;

import java.util.List;

import assets.entities.Entity;

public class CollisionMethods {
	private static boolean intersects(Entity a, Entity b) {
		float distance = a.getEuclideanDistance(b);
		if (distance < a.getModel().getRadius() + b.getModel().getRadius()) {
			return true;
		}
		return false;
	}

	// TODO: This is a bit limited at the moment, as it assumes that we're
	// always testing against all entities
	// TODO: Checking the null status of GLModels like this sucks. Really need
	// to make sure the entity list is accurate
	public static void checkEntityCollisions(List<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getModel() == null) {
				continue;
			}
			for (int j = i + 1; j < entities.size(); j++) {
				if (entities.get(j).getModel() != null) {
					if (CollisionMethods.intersects(entities.get(i), entities.get(j))) {
						entities.get(i).collidedWith(entities.get(j));
						entities.get(j).collidedWith(entities.get(i));
					}
				}
			}
		}
	}
}
