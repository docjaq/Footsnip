package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.FootsnipProperties;
import main.GameControl;
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

import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glmodels.factories.GLDefaultProjectileFactory;
import renderer.glshaders.GLGaussianShader;
import renderer.glshaders.GLGaussianTessellationShader;
import renderer.glshaders.GLShader;
import renderer.glshaders.GLWaterShader;
import samplers.CubeMap;
import samplers.Texture2D;
import thread.RendererThread;
import assets.AssetContainer;
import assets.entities.Monster;
import assets.entities.MonsterFactory;
import assets.entities.Player;
import assets.entities.PolygonalScenery;
import assets.entities.PolygonalSceneryFactory;
import assets.entities.Projectile;
import assets.world.AbstractTile;
import assets.world.PolygonHeightmapTileFactory;
import assets.world.datastructures.TileDataStructure2D;
import camera.CameraModel.MouseButton;
import camera.CameraModel.ObjectData;
import camera.CameraModel.ObjectPole;
import camera.CameraModel.ViewData;
import camera.CameraModel.ViewPole;
import camera.CameraModel.ViewScale;
import camera.CameraUtils;
import exception.RendererException;

public class Renderer_4_0 extends RendererThread {

	private final String[] DEFAULT_SHADER_LOCATION = { "resources/shaders/lighting/gaussian_vert.glsl",
			"resources/shaders/lighting/gaussian_frag.glsl" };

	private final String[] WATER_SHADER_LOCATION = { "resources/shaders/water/water_vert.glsl", "resources/shaders/water/water_frag.glsl" };

	private final String[] GAUSSIAN_TESS_SHADER_LOCATION = { "resources/shaders/tessellation/gaussian_vert.glsl",
			"resources/shaders/tessellation/terrain_tessCont.glsl", "resources/shaders/tessellation/terrain_tessEval.glsl",
			"resources/shaders/tessellation/terrain_geom.glsl", "resources/shaders/tessellation/gaussian_frag.glsl", };

	// Something's not currently right with this. It's either not mapping the
	// correct images to the correct cube faces, or my vector computation in the
	// shader is not correct
	private final String[] CUBE_MAP_LOCATION = { "resources/cubemaps/Maskonaive/posx.png", "resources/cubemaps/Maskonaive/negx.png",
			"resources/cubemaps/Maskonaive/negy.png", "resources/cubemaps/Maskonaive/posy.png", "resources/cubemaps/Maskonaive/posz.png",
			"resources/cubemaps/Maskonaive/negz.png" }; // x, y, down, up

	private final String NORMALMAP_A_LOCATION = "resources/normalmaps/Terrain3.png";

	private final String NORMALMAP_WATER_LOCATION = "resources/normalmaps/Water0.png";

	// Setup variables
	private final String WINDOW_TITLE = "Footsnip";
	private final int WIDTH = FootsnipProperties.getWidth();
	private final int HEIGHT = FootsnipProperties.getHeight();

	private final int MAX_FPS = 500;

	private Map<Class<?>, GLShader> shaderMap;
	private CubeMap cubeMap;

	private Class<GLGaussianShader> defaultShaderClass = GLGaussianShader.class;
	private Class<GLGaussianTessellationShader> tessellationShaderClass = GLGaussianTessellationShader.class;
	private Class<GLWaterShader> waterShaderClass = GLWaterShader.class;

	// The time of the last frame, to calculate the time delta for rotating
	// monsters.
	private long lastFrameTime;

	/** The time we started counting frames. */
	private long lastFPSUpdate;

	/** The number of frames rendered since lastFPSUpdate. */
	private int framesThisSecond;

	// http://www.arcsynthesis.org/gltut/Positioning/Tut07%20Shared%20Uniforms.html
	private int projectionUniformBuffer; // Default rendering buffer
	private final int projectionBlockIndex = 0;
	private ViewPole viewPole;
	private ObjectPole objectPole;

	public Renderer_4_0(AssetContainer assContainer, Main mainApplication) {
		super(assContainer, mainApplication);

		// Initialise FPS calculation fields.
		framesThisSecond = 0;
		lastFPSUpdate = CameraUtils.getTime();
		getFrameTimeDelta();
	}

	protected void beforeLoop() throws RendererException {
		float[] backgroundColor = { 1f, 1f, 1f, 1.0f };
		projectionUniformBuffer = setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE, backgroundColor, projectionBlockIndex);

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

		// Load textures

		cubeMap = new CubeMap(CUBE_MAP_LOCATION, 2048, 2048, GL11.GL_RGB);
		int normalMapALocation = GLUtilityMethods.loadPNGTextureAsDataAndBind(NORMALMAP_A_LOCATION, 4);

		Texture2D waterNormalMap = new Texture2D(NORMALMAP_WATER_LOCATION, 4);
		// int normalMapWaterLocation =
		// GLUtilityMethods.loadPNGTextureAsDataAndBind(NORMALMAP_WATER_LOCATION,
		// 4);

		shaderMap = new HashMap<Class<?>, GLShader>();

		// Load default shader
		GLShader currentShader = new GLGaussianShader(projectionBlockIndex);
		currentShader.create(DEFAULT_SHADER_LOCATION);
		shaderMap.put(defaultShaderClass, currentShader);

		// Load terrain tessellation shader
		GLShader tessellationShader = new GLGaussianTessellationShader(projectionBlockIndex);
		tessellationShader.create(GAUSSIAN_TESS_SHADER_LOCATION);
		tessellationShader.bindShader();
		tessellationShader.copyShaderSpecificUniformsToShaderInit();
		((GLGaussianTessellationShader) tessellationShader).setNormalMapALocation(normalMapALocation);
		tessellationShader.unbindShader();
		shaderMap.put(tessellationShaderClass, tessellationShader);

		// Load water shader
		GLShader waterShader = new GLWaterShader(projectionBlockIndex, cubeMap, waterNormalMap);
		waterShader.create(WATER_SHADER_LOCATION);
		waterShader.bindShader();
		waterShader.copyShaderSpecificUniformsToShaderInit();
		waterShader.unbindShader();
		shaderMap.put(waterShaderClass, waterShader);

		// ((GLGaussianTessellationShader)
		// tessellationShader).bindSamplerUnit();

		createEntities();
		createWorld();
		createScenery();

	}

	protected void afterLoop() {
		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(assContainer.getPlayer());
	}

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

		Vector4 worldLightPos = new Vector4(assContainer.getPlayer().getPosition().modelPos.x(),
				(assContainer.getPlayer().getPosition().modelPos.y()), assContainer.getPlayer().getPosition().modelPos.z() + 0.3f, 1);

		// Confused why I don't need to push/pop this...
		Vector4 lightPosCameraSpace = modelMatrix.getTop().mult(worldLightPos);

		// Copy shared matrices to shader
		currentShader.copySharedUniformsToShader(lightPosCameraSpace, new MaterialParams());
		{
			modelMatrix.pushMatrix();
			{
				renderPlayer(assContainer.getPlayer(), currentShader, modelMatrix);
				assContainer.getTileDataStructure().drawEntities(currentShader, objectPole, modelMatrix, assContainer.getPlayer(),
						Monster.class);
				assContainer.getTileDataStructure().drawEntities(currentShader, objectPole, modelMatrix, assContainer.getPlayer(),
						Projectile.class);
			}
			modelMatrix.popMatrix();
		}
		currentShader.unbindShader();

		{
			modelMatrix.pushMatrix();
			{
				currentShader = shaderMap.get(tessellationShaderClass);
				currentShader.bindShader();
				currentShader.copySharedUniformsToShader(lightPosCameraSpace, new MaterialParams());
				renderTilesTerrain(assContainer.getTileDataStructure(), currentShader, modelMatrix, assContainer.getPlayer());
				currentShader.unbindShader();

				currentShader = shaderMap.get(waterShaderClass);
				currentShader.bindShader();
				currentShader.copySharedUniformsToShader(lightPosCameraSpace, new MaterialParams(0.8f));
				renderTilesWater(assContainer.getTileDataStructure(), currentShader, modelMatrix, assContainer.getPlayer());
				currentShader.unbindShader();
			}
			modelMatrix.popMatrix();
		}

		exitOnGLError("logicCycle");
	}

	private void createWorld() throws RendererException {
		Vector3 tilePos = new Vector3(0, 0, 0);
		Vector3 tileAngle = new Vector3(0, 0, 0);
		float tileScale = 1f;
		GLPosition position = new GLPosition(tilePos, tileAngle, tileScale, 0);

		PolygonHeightmapTileFactory glTileFactory = new PolygonHeightmapTileFactory(129, assContainer.getTileDataStructure());
		AbstractTile initialTile = glTileFactory.create(null, position);

		assContainer.getTileDataStructure().init(glTileFactory, initialTile);
	}

	private void createScenery() {
		// Hardcoded because Dave's mother is a prostitute

		assContainer.setPolygonalSceneries(new ArrayList<PolygonalScenery>());
		Vector3 sceneryPos = new Vector3(0.05f, 0.05f, 0);
		assContainer.addPolygonalScenery(PolygonalSceneryFactory.create(sceneryPos));
	}

	private void createEntities() throws RendererException {

		Vector3 playerPos = new Vector3(0, 0, 0);
		Vector3 playerAngle = new Vector3(0, 0, 0);
		float playerScale = 1f;
		Vector4 playerColor = new Vector4(0.6f, 0.6f, 0.0f, 1.0f);

		Ply playerMesh = new Ply();
		playerMesh.read(new File("resources/meshes/SpaceFighter_small.ply"), playerColor);
		GLModel playerModel = new GLMesh(playerMesh.getTriangles(), playerMesh.getVertices());
		GLPosition playerPosition = new GLPosition(playerPos, playerAngle, playerScale, playerModel.getModelRadius());

		assContainer.setPlayer(new Player(playerModel, playerPosition, 0, new float[] { 1.0f, 0.0f, 0.0f }));
		assContainer.setMonsters(new ArrayList<Monster>());

		Vector4 monsterColor = new Vector4(1.0f, 0.6f, 0.0f, 1.0f);
		Ply monsterMesh = new Ply();
		monsterMesh.read(new File("resources/meshes/SmoothBlob_small.ply"), monsterColor);

		String script = "resources/lua/monsters.lua";
		LuaValue _G = JsePlatform.standardGlobals();
		_G.get("dofile").call(LuaValue.valueOf(script));
		LuaValue getRotationDelta = _G.get("getRotationDelta");

		MonsterFactory monsterFactory = new MonsterFactory(monsterMesh);

		float spread = 30;
		for (int i = 0; i < 500; i++) {
			Vector3 monsterPos = new Vector3((float) (Math.random() - 0.5f) * spread, (float) (Math.random() - 0.5f) * spread, 0);
			float rotationDelta = getRotationDelta.call(LuaValue.valueOf(i)).tofloat();
			assContainer.addMonster(monsterFactory.create(monsterPos, rotationDelta));
		}

		// Initialise projectile factory
		assContainer.setProjectileFactory(GLDefaultProjectileFactory.getInstance());
	}

	private void renderTilesTerrain(TileDataStructure2D dataStructure, GLShader shader, MatrixStack modelMatrix, Player player) {
		dataStructure.drawTerrain(shader, objectPole, modelMatrix, player);
	}

	private void renderTilesWater(TileDataStructure2D dataStructure, GLShader shader, MatrixStack modelMatrix, Player player) {
		dataStructure.drawWater(shader, objectPole, modelMatrix, player);
	}

	private void renderPlayer(Player player, GLShader shader, MatrixStack modelMatrix) {
		modelMatrix.pushMatrix();
		{
			modelMatrix.getTop().mult(objectPole.calcMatrix());
			player.getModel().draw(shader, modelMatrix, player.getPosition());
		}
		modelMatrix.popMatrix();
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

        waitForWindowClosed();
    }

    private void waitForWindowClosed() {
        if (Display.isCloseRequested()) {
            GameControl.stopGame();
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
