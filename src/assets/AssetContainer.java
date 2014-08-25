package assets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import physics.PhysicsEngine;
import renderer.glmodels.factories.GLProjectileFactory;
import assets.entities.Monster;
import assets.entities.Player;
import assets.entities.PolygonalScenery;
import assets.entities.Projectile;
import assets.world.datastructures.HashmapTileDataStructure2D;
import assets.world.datastructures.TileDataStructure2D;

public class AssetContainer {

	private Player player;
	private List<Monster> monsters;
	private List<Projectile> projectiles;
	private List<PolygonalScenery> polygonalSceneries;
	private TileDataStructure2D tiles;
	private PhysicsEngine physicsEngine;

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

	public AssetContainer() {
		monsters = new ArrayList<Monster>(0);
		// So the list can be rendered and added to at the same time
		projectiles = new CopyOnWriteArrayList<Projectile>();
		tiles = new HashmapTileDataStructure2D(this);
	}

	/** AbstractTiles **/

	public TileDataStructure2D getTileDataStructure() {
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

	public List<PolygonalScenery> getPolygonalSceneries() {
		return polygonalSceneries;
	}

	public void setPolygonalSceneries(List<PolygonalScenery> polygonalSceneries) {
		this.polygonalSceneries = polygonalSceneries;
	}

	public void addPolygonalScenery(PolygonalScenery polygonalScenery) {
		this.polygonalSceneries.add(polygonalScenery);
	}

	public PhysicsEngine getPhysicsEngine() {
		return physicsEngine;
	}

	public void setPhysicsEngine(PhysicsEngine physicsEngine) {
		this.physicsEngine = physicsEngine;
	}
}
