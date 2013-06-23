package assets;

import java.util.List;

import assets.entities.Monster;
import assets.entities.Player;

public class AssetContainer {

	private Player player;
	private List<Monster> monsters;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}

	public Player getPlayer() {
		return player;
	}

	public List<Monster> getMonsters() {
		return monsters;
	}
}
