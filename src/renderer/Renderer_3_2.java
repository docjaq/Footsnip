package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.Main;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glshaders.GLPhongShader;
import renderer.glshaders.GLShader;
import thread.RendererThread;
import util.Utils;
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

	private final int MAX_FPS = 60;

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
		int frameDelta = getFrameTimeDelta();
		for (Monster m : assContainer.getMonsters()) {
			m.rotate(frameDelta);
		}
		// assContainer.getPlayer().rotate(frameDelta);

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

		// GLShader texturedShader = new GLTexturedShader();
		// texturedShader.create(TEX_SHADER_NAME);
		// System.out.println("Textured shader ID " +
		// texturedShader.getProgramID());

		/**
		 * BUG: Something is really wrong here. If I create a GLNormalTest model
		 * for the monsters, it renders correctly. For the player, it does not.
		 * Any more complex geometry and it's broken for both. Something is
		 * wrong with the way that the VBOs are being generated. And perhaps
		 * some other bug in the player class.
		 **/

		Vector3f playerPos = new Vector3f(0, 0, 0);
		Vector3f playerAngle = new Vector3f(0, 0, 00);
		Vector3f playerScale = new Vector3f(0.07f, 0.07f, 0.07f);
		float[] playerColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		// GLModel model = new GLTexturedQuad(modelPos, modelAngle, modelScale,
		// texturedShader, modelColor, PLAYER_TEXTURE);
		GLModel playerModel = new GLMesh(new File("resources/meshes/SpaceFighter.ply"), playerPos, playerAngle, playerScale, phongShader,
				playerColor);

		assContainer.setPlayer(new Player(playerModel, "Dave the Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f }));

		assContainer.setMonsters(new ArrayList<Monster>());

		for (int i = 0; i < 20; i++) {
			Vector3f monsterPos = new Vector3f((float) Math.random() * 40f - 25f, (float) Math.random() * 30f - 15f, 0);
			Vector3f monsterAngle = new Vector3f(0, 0, 0);
			Vector3f monsterScale = new Vector3f(0.03f, 0.03f, 0.03f);
			float[] monsterColor = { (float) Math.random(), (float) Math.random(), (float) Math.random(), (float) 1 };
			// String texture = MONSTER_TEXTURES[(int) Math.floor(Math.random()
			// * 4)];
			GLModel monsterModel = new GLMesh(new File("resources/meshes/dodecahedron.ply"), monsterPos, monsterAngle, monsterScale,
					phongShader, monsterColor);
			Monster monster = new Monster(monsterModel, "Monster_" + i, 0);
			monster.setRotationDelta((float) Math.random() * 5f - 2.5f);
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
		 * matrices once. A better solution would be to grab it from some parent
		 * container I guess
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
		// if (Math.random() > 0.99) {
		// maximumFrameRate = new Random().nextInt(60) + 20;
		// }

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
