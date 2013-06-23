package assets;

import java.util.ArrayList;
import java.util.List;

import assets.entities.Entity;
import assets.entities.Monster;
import assets.entities.Player;

public class AssetContainer {

	private Player player;
	private List<Monster> monsters;

	private List<Entity> entities;

	public AssetContainer() {
		monsters = new ArrayList<Monster>(0);
	}

	public List<Entity> getEntities() {
		return entities;
	}

	private void updateEntities() {
		entities = new ArrayList<Entity>(monsters.size() + 1);
		entities.add(player);
		entities.addAll(monsters);
	}

	public void setPlayer(Player player) {
		this.player = player;
		updateEntities();
	}

	public void addMonster(Monster monster) {
		this.monsters.add(monster);
		entities.add(monster);
	}

	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
		updateEntities();
	}

	public Player getPlayer() {
		return player;
	}

	public List<Monster> getMonsters() {
		return monsters;
	}
}
