package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.Main;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLCube;
import renderer.glmodels.GLModel;
import renderer.glmodels.GLTexturedQuad;
import renderer.glshaders.GLPhongShader;
import renderer.glshaders.GLShader;
import renderer.glshaders.GLTexturedShader;
import thread.RendererThread;
import assets.AssetContainer;
import assets.Monster;
import assets.Player;
import exception.RendererException;

public class Renderer_3_2 extends RendererThread {

	private final String[] GEN_SHADER_NAME = { "resources/shaders/general/vertex.glsl", "resources/shaders/general/fragment.glsl" };
	private final String[] TEX_SHADER_NAME = { "resources/shaders/textured/vertex.glsl", "resources/shaders/textured/fragment.glsl" };
	private final String[] PHONG_SHADER_NAME = { "resources/shaders/phonglighting/vertex.glsl",
			"resources/shaders/phonglighting/fragment.glsl" };

	// ATTENTION: Just for now
	private final String PLAYER_TEXTURE = "resources/images/ship.png";
	private final String[] MONSTER_TEXTURES = { "resources/images/virus1.png", "resources/images/virus2.png",
			"resources/images/virus3.png", "resources/images/Bacteria.png" };

	// Setup variables
	private final String WINDOW_TITLE = "Footsnip";
	private final int WIDTH = 1024;
	private final int HEIGHT = 768;

	private int maximumFrameRate = 60;

	private GLWorld glWorld;

	/** The time of the last frame, to calculate the delta. */
	private long lastFrameTime;

	/** The time we started counting frames. */
	private long lastFPSUpdate;

	/** The number of frames rendered since lastFPSUpdate. */
	private int framesThisSecond;

	public Renderer_3_2(AssetContainer assContainer, Main mainApplication) {
		super(assContainer, mainApplication);

		// Initialise FPS calculation fields.
		framesThisSecond = 0;
		lastFrameTime = getTime();
		lastFPSUpdate = getTime();
	}

	protected void beforeLoop() throws RendererException {
		float[] backgroundColor = { 0.0f, 0.0f, 0.0f, 1.0f };
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE, backgroundColor);

		// Camera is actually static at this stage
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		createEntities();
	}

	protected void afterLoop() {
		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(glWorld, assContainer.getPlayer());
	}

	// TODO: Sort this method out so we can apply transforms and shit to our
	// 'data structure' of models. Currently it's pretty hard-coded to our
	// texturedQuad model
	private void logicCycle() {
		for (Monster m : assContainer.getMonsters()) {
			m.rotate(assContainer.getFrameDelta());
		}

		// -- Update matrices
		// Reset view and model matrices
		glWorld.clearViewMatrix();

		// Translate camera
		glWorld.transformCamera();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		renderPlayer(assContainer.getPlayer());
		renderMonsters(assContainer.getMonsters());

		exitOnGLError("logicCycle");
	}

	// Debug method for creating some test stuff
	private void createEntities() throws RendererException {
		GLShader phongShader = new GLPhongShader();
		phongShader.create(PHONG_SHADER_NAME);
		System.out.println("Phong shader ID " + phongShader.getProgramID());

		GLShader texturedShader = new GLTexturedShader();
		texturedShader.create(TEX_SHADER_NAME);
		System.out.println("Textured shader ID " + texturedShader.getProgramID());

		Vector3f modelPos = new Vector3f(0, 0, 0);
		Vector3f modelAngle = new Vector3f(0, 0, 0);
		Vector3f modelScale = new Vector3f(0.05f, 0.05f, 0.05f);
		float[] modelColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		GLModel model = new GLTexturedQuad(modelPos, modelAngle, modelScale, texturedShader, modelColor, PLAYER_TEXTURE);

		assContainer.setPlayer(new Player(model, "Dave the Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f }));

		assContainer.setMonsters(new ArrayList<Monster>());
		// ATTENTION: Takes some time to load as it's re-loading the texture for
		// EVERY model :)
		for (int i = 0; i < 30; i++) {
			Vector3f monsterPos = new Vector3f((float) Math.random() * 20f - 10f, (float) Math.random() * 14f - 7f, 0);
			Vector3f monsterAngle = new Vector3f(0, 0, 0);
			Vector3f monsterScale = new Vector3f(0.05f, 0.05f, 0.05f);
			float[] monsterColor = { (float) Math.random(), (float) Math.random(), (float) Math.random(), (float) 1 /*
																													 * Math
																													 * .
																													 * random
																													 * (
																													 * )
																													 */};
			String texture = MONSTER_TEXTURES[(int) Math.floor(Math.random() * 4)];
			GLModel monsterModel = new GLCube(monsterPos, monsterAngle, monsterScale, phongShader, monsterColor, texture);
			Monster monster = new Monster(monsterModel, "Monster_" + i, 0);
			monster.setRotationDelta((float) Math.random() * 0.2f - 0.1f);
			assContainer.getMonsters().add(monster);
		}
	}

	private void renderPlayer(Player player) {
		glWorld.copyCameraMatricesToShader(player.getModel().getShader());
		player.getModel().draw();
	}

	private void renderMonsters(List<Monster> monsters) {
		/**
		 * All the getShader() method calls for the monsters points to the same
		 * shader, so just grab it from the first monster and set up the
		 * matricies once. A better solution would be to grab it from some
		 * parent container I guess
		 */
		glWorld.copyCameraMatricesToShader(monsters.get(0).getModel().getShader());
		for (Monster m : monsters) {
			m.getModel().draw();
		}
	}

	public void gameLoop() {
		// Update logic and render
		logicCycle();

		exitOnGLError("loopCycle");

		// TODO: Just for debugging, randomly vary the frame rate with a 1/100
		// chance per frame.
		if (Math.random() > 0.99) {
			maximumFrameRate = new Random().nextInt(60) + 20;
		}

		// Force a maximum FPS.
		Display.sync(maximumFrameRate);

		// Let the CPU synchronize with the GPU if GPU is tagging behind
		Display.update();

		updateFPS();
		assContainer.setFrameDelta(getDelta());

		// TODO: Should this really be in the renderer?
		if (Display.isCloseRequested()) {
			mainApplication.quitGame();
		}
	}

	/**
	 * Calculate the time delta between now and the previous frame.
	 * 
	 * @return Milliseconds since the last frame.
	 */
	private int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrameTime);
		lastFrameTime = time;

		return delta;
	}

	/**
	 * Gets the current time in milliseconds, using LWJGLs high resolution
	 * timer.
	 * 
	 * @return The current time in milliseconds.
	 */
	private long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	/**
	 * Calculate the frames per second, by counting the number of frames every
	 * second, and resetting that count at the end of each second.
	 * 
	 * TODO: Just displaying it in the title bar for now.
	 */
	private void updateFPS() {
		if (getTime() - lastFPSUpdate > 1000) {
			Display.setTitle("FPS: " + framesThisSecond);
			framesThisSecond = 0;
			lastFPSUpdate += 1000;
		}

		framesThisSecond++;
	}

}
