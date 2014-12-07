package assets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import assets.entities.NonPlayer;
import physics.DefaultPhysicsEngine;
import renderer.glmodels.factories.GLProjectileFactory;
import assets.entities.Player;
import assets.entities.Projectile;
import assets.world.datastructures.HashmapTileDataStructure2D;
import assets.world.datastructures.TileDataStructure2D;

public class AssetContainer {

	private Player player;
	private List<NonPlayer> nonPlayers;
	private List<Projectile> projectiles;
	private TileDataStructure2D tiles;
	private DefaultPhysicsEngine physicsEngine;

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
		nonPlayers = new ArrayList<>(0);
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

	/** Non Players **/

	public void addNonPlayers(NonPlayer nonPlayer) {
		this.nonPlayers.add(nonPlayer);
	}

	public void setNonPlayers(List<NonPlayer> nonPlayers) {
		this.nonPlayers = nonPlayers;
	}

	public List<NonPlayer> getNonPlayers() {
		return nonPlayers;
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

	public DefaultPhysicsEngine getPhysicsEngine() {
		return physicsEngine;
	}

	public void setPhysicsEngine(DefaultPhysicsEngine physicsEngine) {
		this.physicsEngine = physicsEngine;
	}
}
