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
import math.types.MatrixStack;
import math.types.Quaternion;
import math.types.Vector3;
import math.types.Vector4;
import mesh.Ply;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import renderer.glmodels.GLDefaultProjectileFactory;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glmodels.GLTileFactory;
import renderer.glmodels.GLTileMidpointDisplacementFactory;
import renderer.glshaders.GLGaussianShader;
import renderer.glshaders.GLShader;
import thread.RendererThread;
import assets.AssetContainer;
import assets.entities.Entity;
import assets.entities.Monster;
import assets.entities.MonsterFactory;
import assets.entities.Player;
import assets.entities.PolygonalScenery;
import assets.entities.PolygonalSceneryFactory;
import assets.entities.Projectile;
import assets.world.PolygonHeightmapTile;
import assets.world.datastructures.TileDataStructure2D;
import camera.CameraModel.MouseButton;
import camera.CameraModel.ObjectData;
import camera.CameraModel.ObjectPole;
import camera.CameraModel.ViewData;
import camera.CameraModel.ViewPole;
import camera.CameraModel.ViewScale;
import camera.CameraUtils;
import exception.RendererException;

public class Renderer_3_2 extends RendererThread {

	private final String[] DEFAULT_SHADER_LOCATION = { "resources/shaders/lighting/gaussian_vert.glsl",
			"resources/shaders/lighting/gaussian_frag.glsl" };

	// Setup variables
	private final String WINDOW_TITLE = "Footsnip";
	private final int WIDTH = 1920;
	private final int HEIGHT = 1080;

	private final int MAX_FPS = 400;

	private Map<Class<?>, GLShader> shaderMap;

	private Class<GLGaussianShader> defaultShaderClass = GLGaussianShader.class;

	// The time of the last frame, to calculate the time delta for rotating
	// monsters.
	private long lastFrameTime;

	/** The time we started counting frames. */
	private long lastFPSUpdate;

	/** The number of frames rendered since lastFPSUpdate. */
	private int framesThisSecond;

	private int projectionUniformBuffer;
	private final int projectionBlockIndex = 2;
	private ViewPole viewPole;
	private ObjectPole objectPole;
	float startTime = 0;

	public Renderer_3_2(AssetContainer assContainer, Main mainApplication) {
		super(assContainer, mainApplication);

		// Initialise FPS calculation fields.
		framesThisSecond = 0;
		lastFPSUpdate = CameraUtils.getTime();
		getFrameTimeDelta();
	}

	protected void beforeLoop() throws RendererException {
		float[] backgroundColor = { 1f, 1f, 1f, 1.0f };
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE, backgroundColor, projectionUniformBuffer, projectionBlockIndex);

		// Vector3: Target position of camera focus
		// Quaternion: Orientation of the camera relative to the target
		// Distance (or radius) from target
		// Rotation around target/position vector
		ViewData viewData = new ViewData(new Vector3(0, 0.0f, 0f), new Quaternion(0.0f, 0, 0, 1f), 1.4f, 0);

		ViewScale viewScale = new ViewScale(0.2f, 20, 1.5f, 0.5f, 0, 0, 90f / 250f);

		// Setup initial transform for object in MODEL space
		ObjectData objectData = new ObjectData(new Vector3(0, 0.0f, 0), new Quaternion());

		// Set the viewing stuff AND the object stuff in the MousePoles class
		viewPole = new ViewPole(viewData, viewScale, MouseButton.LEFT_BUTTON);
		objectPole = new ObjectPole(objectData, 90f / 250f, MouseButton.RIGHT_BUTTON, viewPole);

		shaderMap = new HashMap<Class<?>, GLShader>();

		GLShader currentShader = new GLGaussianShader(projectionBlockIndex);
		currentShader.create(DEFAULT_SHADER_LOCATION);
		shaderMap.put(defaultShaderClass, currentShader);

		createEntities(currentShader);
		createWorld(currentShader);
		createScenery(currentShader);

	}

	protected void afterLoop() {
		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(assContainer.getPlayer());
	}

	// private Vector4 calcLightPosition() {
	// float lightHeight = 0.1f, lightRadius = 0.5f;
	// startTime += 0.003;
	//
	// Vector4 ret = new Vector4(1.0f, 0.0f, lightHeight, 1);
	// ret.x((float) Math.cos(startTime * 2 * Math.PI) * lightRadius);
	// ret.y((float) Math.sin(startTime * 2 * Math.PI) * lightRadius);
	//
	// return ret;
	// }

	private void logicCycle() {

		CameraUtils.updateMousePoles(viewPole, objectPole);
		// Translate camera to new Player position
		viewPole.setTargetPos(assContainer.getPlayer().getPosition().modelPos);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		GLShader currentShader = shaderMap.get(defaultShaderClass);
		currentShader.bindShader();

		MatrixStack modelMatrix = new MatrixStack();

		// Judging by how it's used, this 'model matrix' must be the
		// modelToCamera matrix
		modelMatrix.setTop(viewPole.calcMatrix());

		// GLModel model = assContainer.getPlayer().getModel();
		Vector4 worldLightPos = new Vector4(assContainer.getPlayer().getPosition().modelPos.x(),
				(assContainer.getPlayer().getPosition().modelPos.y()), assContainer.getPlayer().getPosition().modelPos.z() + 0.3f, 1);
		// Vector4 worldLightPos = calcLightPosition();

		// Confused why I don't need to push/pop this...
		Vector4 lightPosCameraSpace = modelMatrix.getTop().mult(worldLightPos);

		// Copy shared matrices to shader
		currentShader.copySharedUniformsToShader(lightPosCameraSpace, new MaterialParams());
		{
			modelMatrix.pushMatrix();
			{
				renderPlayer(assContainer.getPlayer(), currentShader, modelMatrix);
				renderMonsters(assContainer.getMonsters(), currentShader, modelMatrix);
				renderTiles(assContainer.getTileDataStructure(), currentShader, modelMatrix);
				renderScenery(assContainer.getPolygonalSceneries(), currentShader, modelMatrix);
				renderProjectiles(assContainer.getProjectiles(), currentShader, modelMatrix);
			}
			modelMatrix.popMatrix();
		}

		currentShader.unbindShader();

		exitOnGLError("logicCycle");
	}

	private void createWorld(GLShader shader) throws RendererException {
		Vector3 tilePos = new Vector3(0, 0, 0);
		Vector3 tileAngle = new Vector3(0, 0, 0);
		float tileScale = 1f;
		GLPosition position = new GLPosition(tilePos, tileAngle, tileScale, 0);

		PolygonHeightmapTile initialTile = new PolygonHeightmapTile(null, null, position);
		GLTileFactory glTileFactory = new GLTileMidpointDisplacementFactory(129, assContainer.getTileDataStructure());
		GLModel model = glTileFactory.create(initialTile);
		initialTile.setModel(model);

		assContainer.getTileDataStructure().init(glTileFactory, initialTile);
	}

	private void createScenery(GLShader shader) {
		// Hardcoded because Dave's mother is a prostitute

		assContainer.setPolygonalSceneries(new ArrayList<PolygonalScenery>());
		Vector3 sceneryPos = new Vector3(0.05f, 0.05f, 0);
		assContainer.addPolygonalScenery(PolygonalSceneryFactory.create(shader, sceneryPos));
	}

	private void createEntities(GLShader shader) throws RendererException {

		Vector3 playerPos = new Vector3(0, 0, 0);
		Vector3 playerAngle = new Vector3(0, 0, 0);
		float playerScale = 1f;
		Vector4 playerColor = new Vector4(0.4f, 0.4f, 1.0f, 1.0f);

		Ply playerMesh = new Ply();
		playerMesh.read(new File("resources/meshes/SpaceFighter_small.ply"), playerColor);
		GLModel playerModel = new GLMesh(playerMesh.getTriangles(), playerMesh.getVertices());
		GLPosition playerPosition = new GLPosition(playerPos, playerAngle, playerScale, playerModel.getModelRadius());

		assContainer.setPlayer(new Player(playerModel, playerPosition, "Dave", 0, new float[] { 1.0f, 0.0f, 0.0f }));
		assContainer.setMonsters(new ArrayList<Monster>());

		Vector4 monsterColor = new Vector4(0.3f, 0.3f, 0.05f, 1.0f);
		Ply monsterMesh = new Ply();
		monsterMesh.read(new File("resources/meshes/SmoothBlob_small.ply"), monsterColor);

		String script = "resources/lua/monsters.lua";
		LuaValue _G = JsePlatform.standardGlobals();
		_G.get("dofile").call(LuaValue.valueOf(script));
		LuaValue getRotationDelta = _G.get("getRotationDelta");

		MonsterFactory monsterFactory = new MonsterFactory(monsterMesh);

		float spread = 2;
		for (int i = 0; i < 50; i++) {
			Vector3 monsterPos = new Vector3((float) (Math.random() - 0.5f) * spread, (float) (Math.random() - 0.5f) * spread, 0);
			float rotationDelta = getRotationDelta.call(LuaValue.valueOf(i)).tofloat();
			assContainer.addMonster(monsterFactory.create(monsterMesh, shader, monsterPos, rotationDelta));
		}

		// Initialise projectile factory
		assContainer.setProjectileFactory(GLDefaultProjectileFactory.getInstance());
	}

	private void renderTiles(TileDataStructure2D dataStructure, GLShader shader, MatrixStack modelMatrix) {
		dataStructure.draw(shader, objectPole, modelMatrix);
	}

	private void renderScenery(List<PolygonalScenery> scenery, GLShader shader, MatrixStack modelMatrix) {
		for (PolygonalScenery s : scenery) {
			modelMatrix.pushMatrix();
			{
				modelMatrix.getTop().mult(objectPole.calcMatrix());
				s.getModel().draw(shader, modelMatrix, s.getPosition());
			}
			modelMatrix.popMatrix();
		}
	}

	private void renderPlayer(Player player, GLShader shader, MatrixStack modelMatrix) {
		modelMatrix.pushMatrix();
		{
			modelMatrix.getTop().mult(objectPole.calcMatrix());
			player.getModel().draw(shader, modelMatrix, player.getPosition());
		}
		modelMatrix.popMatrix();
	}

	private void renderMonsters(List<Monster> monsters, GLShader shader, MatrixStack modelMatrix) {

		List<Entity> toRemove = new ArrayList<Entity>();
		for (Monster m : monsters) {
			if (m.isDestroyable()) {
				toRemove.add(m);
			}
		}
		monsters.removeAll(toRemove);
		for (Monster m : monsters) {
			modelMatrix.pushMatrix();
			{
				modelMatrix.getTop().mult(objectPole.calcMatrix());
				m.getModel().draw(shader, modelMatrix, m.getPosition());
			}
			modelMatrix.popMatrix();
		}

	}

	private void renderProjectiles(List<Projectile> projectiles, GLShader shader, MatrixStack modelMatrix) {

		List<Entity> toRemove = new ArrayList<Entity>();

		if (projectiles.size() > 0) {
			for (Projectile p : projectiles) {
				if (p.isDestroyable()) {
					toRemove.add(p);
				}
			}
			projectiles.removeAll(toRemove);

			for (Projectile p : projectiles) {
				try {
					modelMatrix.pushMatrix();
					{
						modelMatrix.getTop().mult(objectPole.calcMatrix());
						p.getModel().draw(shader, modelMatrix, p.getPosition());
					}
					modelMatrix.popMatrix();
				} catch (NullPointerException e) {
					System.out.println("Exception: Projectile GLModel does not exist");
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
		if (CameraUtils.getTime() - lastFPSUpdate > 1000) {
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
		long time = CameraUtils.getTime();
		int delta = (int) (time - lastFrameTime);
		lastFrameTime = time;

		return delta;
	}
}
