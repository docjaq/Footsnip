package collision;

import java.util.List;

import assets.entities.Entity;

public class CollisionMethods {
	private static boolean intersects(Entity a, Entity b) {
		float distance = a.getEuclideanDistance(b);
		// System.out.print("Dist: " + distance + " - ");
		// System.out.println("r1 = " + a.getModel().getRadius() + ", r2 = " +
		// b.getModel().getRadius());
		if (distance < a.getModel().getRadius() + b.getModel().getRadius()) {
			return true;
		}
		return false;
	}

	public static void checkEntityCollisions(List<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				if (CollisionMethods.intersects(entities.get(i), entities.get(j))) {
					System.out.printf("Entity %d has intersected with entity %d", i, j);
				}
			}
		}
	}
}
