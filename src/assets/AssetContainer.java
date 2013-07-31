package assets;

import java.util.ArrayList;
import java.util.List;

import assets.entities.Entity;
import assets.entities.Monster;
import assets.entities.Player;
import assets.world.datastructures.HashmapDataStructure;
import assets.world.datastructures.TileDataStructure;

public class AssetContainer {

	private Player player;
	private List<Monster> monsters;

	// private List<AbstractTile> tiles;
	private TileDataStructure tiles;

	private List<Entity> entities;

	public AssetContainer() {
		monsters = new ArrayList<Monster>(0);
		tiles = new HashmapDataStructure();
	}

	/** Entities **/

	public List<Entity> getEntities() {
		return entities;
	}

	private void updateEntities() {
		entities = new ArrayList<Entity>(monsters.size() + 1);
		entities.add(player);
		entities.addAll(monsters);
	}

	/** AbstractTiles **/

	public TileDataStructure getTileDataStructure() {
		return tiles;
	}

	/** Player **/

	public void setPlayer(Player player) {
		this.player = player;
		updateEntities();
	}

	public Player getPlayer() {
		return player;
	}

	/** Monsters **/

	public void addMonster(Monster monster) {
		this.monsters.add(monster);
		entities.add(monster);
	}

	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
		updateEntities();
	}

	public List<Monster> getMonsters() {
		return monsters;
	}
}
