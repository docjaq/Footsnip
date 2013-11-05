package assets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import renderer.glmodels.GLProjectileFactory;
import assets.entities.Monster;
import assets.entities.Player;
import assets.entities.Projectile;
import assets.world.datastructures.HashmapDataStructure;
import assets.world.datastructures.TileDataStructure;

public class AssetContainer {

	private Player player;

	private List<Monster> monsters;
	// TODO: Put monster factory here

	private List<Projectile> projectiles;

	public void setProjectiles(List<Projectile> projectiles) {
		this.projectiles = projectiles;
	}

	private GLProjectileFactory projectileFactory;

	public void setProjectileFactory(GLProjectileFactory projectileFactory) {
		this.projectileFactory = projectileFactory;
	}

	public GLProjectileFactory getProjectileFactory() {
		return projectileFactory;
	}

	private TileDataStructure tiles;

	public AssetContainer() {
		monsters = new ArrayList<Monster>(0);
		// So the list can be rendered and added to at the same time
		projectiles = new CopyOnWriteArrayList<Projectile>();
		tiles = new HashmapDataStructure();
	}

	/** AbstractTiles **/

	public TileDataStructure getTileDataStructure() {
		return tiles;
	}

	/** Player **/

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	/** Monsters **/

	public void addMonster(Monster monster) {
		this.monsters.add(monster);
	}

	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}

	public List<Monster> getMonsters() {
		return monsters;
	}

	/** Projectiles **/

	public void addProjectile(Projectile projectile) {
		this.projectiles.add(projectile);
	}

	public void setProjectile(List<Projectile> projectiles) {
		this.projectiles = projectiles;
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}
}
