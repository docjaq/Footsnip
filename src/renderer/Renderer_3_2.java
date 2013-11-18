package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Main;
import mesh.Ply;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.glmodels.GLDefaultProjectileFactory;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import renderer.glmodels.GLTileMidpointDisplacementFactory;
import renderer.glshaders.GLPhongShader;
import renderer.glshaders.GLShader;
import thread.RendererThread;
import util.Utils;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.entities.Monster;
import assets.entities.MonsterFactory;
import assets.entities.Player;
import assets.entities.Projectile;
import assets.world.AbstractTile;
import assets.world.PolygonHeightmapTile;
import assets.world.datastructures.TileDataStructure;
import exception.RendererException;

public class Renderer_3_2 extends RendererThread {

	private final String[] PHONG_SHADER_NAME = { "resources/shaders/phonglighting/vertex.glsl",
			"resources/shaders/phonglighting/fragment.glsl" };

	// Setup variables
	private final String WINDOW_TITLE = "Footsnip";
	private final int WIDTH = 1024;
	private final int HEIGHT = 768;

	private final int MAX_FPS = 200;

	private Map<Class<?>, GLShader> shaderMap;

	/**
	 * The time of the last frame, to calculate the time delta for rotating
	 * monsters.
	 */
	private long lastFrameTime;

	private GLWorld glWorld;

	/** The time we started counting frames. */
	private long lastFPSUpdate;

	/** The number of frames rendered since lastFPSUpdate. */
	private int framesThisSecond;

	public Renderer_3_2(AssetContainer assContainer, Main mainApplication) {
		super(assContainer, mainApplication);

		// Initialise FPS calculation fields.
		framesThisSecond = 0;
		lastFPSUpdate = Utils.getTime();
		getFrameTimeDelta();
	}

	protected void beforeLoop() throws RendererException {
		float[] backgroundColor = { 1f, 1f, 1f, 1.0f };
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE, backgroundColor);

		// Camera is actually static at this stage
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		shaderMap = new HashMap<Class<?>, GLShader>();

		GLPhongShader phongShader = new GLPhongShader();
		phongShader.create(PHONG_SHADER_NAME);
		shaderMap.put(GLPhongShader.class, phongShader);

		createEntities(phongShader);
		createWorld(phongShader);
	}

	protected void afterLoop() {
		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(glWorld, assContainer.getPlayer());
	}

	// TODO: Sort this method out so we can apply transforms and shit to our
	// 'data structure' of models. Currently it's pretty hard-coded to our
	private void logicCycle() {

		// Reset view and model matrices
		glWorld.clearViewMatrix();
		glWorld.setCameraPos(assContainer.getPlayer().getModel().modelPos);

		// Translate camera
		glWorld.transformCamera();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		GLShader currentShader = shaderMap.get(GLPhongShader.class);
		currentShader.bindShader();
		glWorld.copyCameraMatricesToShader(currentShader);

		renderPlayer(assContainer.getPlayer(), currentShader);
		renderMonsters(assContainer.getMonsters(), currentShader);
		renderProjectiles(assContainer.getProjectiles(), currentShader);
		renderTiles(assContainer.getTileDataStructure(), currentShader);
		currentShader.unbindShader();

		exitOnGLError("logicCycle");
	}

	private void createWorld(GLShader shader) throws RendererException {
		Vector3f tilePos = new Vector3f(0, 0, 0);
		Vector3f tileAngle = new Vector3f(0, 0, 0);
		float tileScale = 1f;
		float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };

		PolygonHeightmapTile initialTile = new PolygonHeightmapTile(null, null, tilePos);
		GLTileFactory glTileFactory = new GLTileMidpointDisplacementFactory(129, assContainer.getTileDataStructure());
		GLModel model = glTileFactory.create(initialTile, tilePos, tileAngle, tileScale, tileColor, AbstractTile.SIZE);
		initialTile.setModel(model);

		assContainer.getTileDataStructure().init(glTileFactory, initialTile);
	}

	private void createEntities(GLShader shader) throws RendererException {

		Vector3f playerPos = new Vector3f(0, 0, 0);
		Vector3f playerAngle = new Vector3f(0, 0, 0);
		float playerScale = 1f;
		Vector4f playerColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		float[] playerColorArray = { playerColor.x, playerColor.y, playerColor.z, playerColor.w };

		Ply playerMesh = new Ply();
		playerMesh.read(new File("resources/meshes/SpaceFighter_small.ply"), playerColor);
		/**
		 * TODO: This playerColorArray is sent to the shader, but it's bollocks
		 * and does nothing. Need to either use it in the shader (worked
		 * pre-phong), or remove it from the engine
		 **/
		GLModel playerModel = new GLMesh(playerMesh.getTriangles(), playerMesh.getVertices(), playerPos, playerAngle, playerScale,
				playerColorArray);

		assContainer.setPlayer(new Player(playerModel, "Dave", 0, new float[] { 1.0f, 0.0f, 0.0f }));
		assContainer.setMonsters(new ArrayList<Monster>());

		Vector4f monsterColor = new Vector4f(0.3f, 0.3f, 0.05f, 1.0f);
		Ply monsterMesh = new Ply();
		monsterMesh.read(new File("resources/meshes/SmoothBlob_small.ply"), monsterColor);

		float spread = 5;
		for (int i = 0; i < 300; i++) {
			Vector3f monsterPos = new Vector3f((float) (Math.random() - 0.5f) * spread, (float) (Math.random() - 0.5f) * spread, 0);
			assContainer.addMonster(MonsterFactory.createMesh(monsterMesh, shader, monsterPos));
		}

		// Initialise projectile factory
		assContainer.setProjectileFactory(new GLDefaultProjectileFactory());
	}

	private void renderTiles(TileDataStructure dataStructure, GLShader shader) {
		dataStructure.draw(shader);
	}

	private void renderPlayer(Player player, GLShader shader) {
		player.getModel().draw(shader);
	}

	private void renderMonsters(List<Monster> monsters, GLShader shader) {

		List<Entity> toRemove = new ArrayList<Entity>();
		for (Monster m : monsters) {
			if (m.isDestroyable()) {
				toRemove.add(m);
			}
		}
		monsters.removeAll(toRemove);
		for (Monster m : monsters) {
			m.getModel().draw(shader);
		}
	}

	private void renderProjectiles(List<Projectile> projectiles, GLShader shader) {

		List<Entity> toRemove = new ArrayList<Entity>();

		if (projectiles.size() > 0) {
			for (Projectile p : projectiles) {
				if (p.isDestroyable()) {
					toRemove.add(p);
				} else if (p.getModel() == null) {
					p.createModel(assContainer.getProjectileFactory());
				}
			}
			projectiles.removeAll(toRemove);

			for (Projectile p : projectiles) {
				try {
					p.getModel().draw(shader);
				} catch (NullPointerException e) {
					System.out.println("Exception: GLModel does not exist");
				}

			}
		}
	}

	public void gameLoop() {
		// Update logic and render
		logicCycle();

		exitOnGLError("loopCycle");

		// Force a maximum FPS.
		Display.sync(MAX_FPS);

		// Let the CPU synchronize with the GPU if GPU is tagging behind
		Display.update();

		updateFPS();

		// TODO: Should this really be in the renderer?
		if (Display.isCloseRequested()) {
			mainApplication.quitGame();
		}
	}

	/**
	 * Calculate the frames per second, by counting the number of frames every
	 * second, and resetting that count at the end of each second.
	 * 
	 * TODO: Just displaying it in the title bar for now.
	 */
	private void updateFPS() {
		if (Utils.getTime() - lastFPSUpdate > 1000) {
			Display.setTitle("FPS: " + framesThisSecond);
			framesThisSecond = 0;
			lastFPSUpdate += 1000;
		}

		framesThisSecond++;
	}

	/**
	 * Calculate the time delta between now and the previous frame.
	 * 
	 * @return Milliseconds since the last frame.
	 */
	protected int getFrameTimeDelta() {
		long time = Utils.getTime();
		int delta = (int) (time - lastFrameTime);
		lastFrameTime = time;

		return delta;
	}
}
