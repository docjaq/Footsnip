package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTexturedCube;
import renderer.glmodels.GLTexturedQuad;
import renderer.glshaders.GLGeneralShader;
import renderer.glshaders.GLShader;
import renderer.glshaders.GLTexturedShader;
import thread.GameThread;
import assets.Monster;
import assets.Player;
import control.ControlThread;
import exception.RendererException;

public class Renderer_3_2 {
	// Entry point for the application
	public static void main(String[] args) {
		try {
			new Renderer_3_2();
		} catch (RendererException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	// Setup variables
	private final String WINDOW_TITLE = "Footsnip";
	private final int WIDTH = 1024;
	private final int HEIGHT = 768;

	// ATTENTION: Just for now
	private final String PLAYER_TEXTURE = "resources/images/ship.png";
	private final String[] MONSTER_TEXTURES = { "resources/images/virus1.png", "resources/images/virus2.png",
			"resources/images/virus3.png", "resources/images/Bacteria.png" };

	private final String[] GEN_SHADER_NAME = { "resources/shaders/general/vertex.glsl", "resources/shaders/general/fragment.glsl" };
	private final String[] TEX_SHADER_NAME = { "resources/shaders/textured/vertex.glsl", "resources/shaders/textured/fragment.glsl" };

	// TODO: For now, have a data-structure here of all our entities, etc
	private Player player;

	// TODO: List is a bit shit, need some datastructure here so I can just call
	// draw() transform() etc on the whole datastructure
	private List<Monster> monsters;

	private GLWorld glWorld;

	public Renderer_3_2() throws RendererException {

		float[] backgroundColor = { 0.8f, 0.35f, 0.35f, 1.0f };
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE, backgroundColor);

		// Camera is actually static at this stage
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		createEntities();

		List<GameThread> childThreads = new ArrayList<GameThread>(1);

		// Thread for input.
		GameThread controlThread = new ControlThread(player, 10);
		controlThread.start();
		childThreads.add(controlThread);

		while (!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();

			// update entity stuff
			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		// Nicely stop the child threads.
		for (GameThread thread : childThreads) {
			thread.stopThread();
		}

		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(glWorld, player);
	}

	// Debug method for creating some test stuff
	private void createEntities() throws RendererException {

		GLShader generalShader = new GLGeneralShader();
		generalShader.create(GEN_SHADER_NAME);
		System.out.println("General shader ID " + generalShader.getProgramID());

		GLShader texturedShader = new GLTexturedShader();
		texturedShader.create(TEX_SHADER_NAME);
		System.out.println("Textured shader ID " + texturedShader.getProgramID());

		// start renderer while loop
		Vector3f modelPos = new Vector3f(0, 0, 0);
		Vector3f modelAngle = new Vector3f(0, 0, 0);
		Vector3f modelScale = new Vector3f(0.05f, 0.05f, 0.05f);
		float[] modelColor = { 1.0f, 1.0f, 1.0f };
		GLModel model = new GLTexturedQuad(modelPos, modelAngle, modelScale, modelColor, PLAYER_TEXTURE);
		model.setShader(texturedShader);
		player = new Player(model, "Dave the Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f });

		// ATTENTION: Takes some time to load as it's re-loading the texture for
		// EVERY model :)
		monsters = new ArrayList<Monster>();
		for (int i = 0; i < 30; i++) {
			Vector3f monsterPos = new Vector3f((float) Math.random() * 20f - 10f, (float) Math.random() * 14f - 7f, 0);
			Vector3f monsterAngle = new Vector3f(0, 0, 0);
			Vector3f monsterScale = new Vector3f(0.05f, 0.05f, 0.05f);
			float[] monsterColor = { (float) Math.random(), (float) Math.random(), (float) Math.random() };
			String texture = MONSTER_TEXTURES[(int) Math.floor(Math.random() * 4)];
			GLModel monsterModel = new GLTexturedCube(monsterPos, monsterAngle, monsterScale, monsterColor, texture);
			monsterModel.setShader(generalShader);
			Monster monster = new Monster(monsterModel, "Monster_" + i, 0);
			monster.setRotationDelta((float) Math.random() * 2f - 1f);
			monsters.add(monster);
		}

	}

	// TODO: Sort this method out so we can apply transforms and shit to our
	// 'data structure' of models. Currently it's pretty hard-coded to our
	// texturedQuad model
	private void logicCycle() {
		// Just set up a standard rotation for testing
		// player.rotate();

		for (Monster m : monsters) {
			m.rotate();
		}

		// -- Update matrices
		// Reset view and model matrices
		glWorld.clearViewMatrix();

		// Translate camera
		glWorld.transformCamera();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		renderMonsters();
		renderPlayer();

		exitOnGLError("logicCycle");
	}

	private void renderPlayer() {
		glWorld.copyCameraMatricesToShader(player.getModel().getShader());
		player.getModel().transform();
		player.getModel().getShader().bindShader();
		player.getModel().copyModelMatrixToShader();
		player.getModel().draw();
		player.getModel().getShader().unbindShader();
	}

	private void renderMonsters() {
		/**
		 * The getShader() method points to the same shader, so just grab it
		 * from the first monster. A better solution would be to grab it from
		 * some parent container I guess
		 */
		glWorld.copyCameraMatricesToShader(monsters.get(0).getModel().getShader());
		for (Monster m : monsters) {
			m.getModel().transform();
			m.getModel().getShader().bindShader();
			m.getModel().copyModelMatrixToShader();
			m.getModel().draw();
			m.getModel().getShader().unbindShader();
		}
	}

	private void loopCycle() {
		// Update logic and render
		this.logicCycle();

		exitOnGLError("loopCycle");
	}

}