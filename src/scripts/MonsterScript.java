package scripts;

import math.types.Vector3;
import renderer.GLPosition;
import assets.entities.Monster;
import assets.entities.Player;

public class MonsterScript {

	public static Vector3 update(Monster monster, Player player) {

		Vector3 direction = new Vector3();

		GLPosition monsterPos = monster.getPosition();
		GLPosition playerPos = player.getPosition();

		direction = Vector3.sub(playerPos.getModelPos(), monsterPos.getModelPos());

		return direction.normalize().mult(0.1f);
	}
}
