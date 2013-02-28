package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTexturedQuad;
import assets.Monster;
import assets.Player;
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

	// TODO: For now, have a data-structure here of all our entities, etc
	private Player player;

	// TODO: List is a bit shit, need some datastructure here so I can just call
	// draw() transform() etc on the whole datastructure
	private List<Monster> monsters;

	private GLWorld glWorld;

	public Renderer_3_2() throws RendererException {

		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE);

		// Camera is actually static at this stage
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		createEntities();

		while (!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();

			// update entity stuff
			// Force a maximum FPS of about 60
			Display.sync(0);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		// TODO: Modify this to accept the whole entity data-structure
		destroyOpenGL(glWorld, player);
	}

	// Debug method for creating some test stuff
	private void createEntities() {
		// start renderer while loop
		Vector3f modelPos = new Vector3f(0, 0, 0);
		Vector3f modelAngle = new Vector3f(0, 0, 0);
		Vector3f modelScale = new Vector3f(0.1f, 0.1f, 0.1f);
		GLModel model = new GLTexturedQuad(modelPos, modelAngle, modelScale);
		player = new Player(model, "Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f });

		monsters = new ArrayList<Monster>();
		for (int i = 0; i < 5; i++) {
			Vector3f monsterPos = new Vector3f((float) Math.random() * 10, (float) Math.random() * 10, 0);
			Vector3f monsterAngle = new Vector3f(0, 0, 0);
			float scale = ((float) Math.random() * 0.5f);
			Vector3f monsterScale = new Vector3f(scale, scale, 0.1f);
			GLModel monsterModel = new GLTexturedQuad(monsterPos, monsterAngle, monsterScale);
			monsters.add(new Monster(monsterModel, "Monster_" + i, 0, new float[] { (float) Math.random(), (float) Math.random(),
					(float) Math.random() }));
		}

	}

	// TODO: Sort this method out so we can apply transforms and shit to our
	// 'data structure' of models. Currently it's pretty hard-coded to our
	// texturedQuad model
	private void logicCycle() {
		// -- Input processing

		// Allows you to hold the key down
		Keyboard.enableRepeatEvents(true);

		/*
		 * if (Keyboard.isKeyDown(Keyboard.KEY_1) && !Keyboard.isRepeatEvent())
		 * textureSelector = 0; if (Keyboard.isKeyDown(Keyboard.KEY_2) &&
		 * !Keyboard.isRepeatEvent()) textureSelector = 1;
		 */

		// Modify player model
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			player.moveLeft();
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			player.moveRight();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			player.moveDown();
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			player.moveUp();
		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD))
			player.increaseScale();
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA))
			player.decreaseScale();
		// Just set up a standard rotation for testing
		player.rotate();

		// for (Monster m : monsters) {
		// m.rotate();
		// }

		// -- Update matrices
		// Reset view and model matrices
		glWorld.clearMatricies();

		// Translate camera
		glWorld.transformCamera();

		// Weird to send a non GL object (entity) to this, rather than the
		// GLModel object... not quite right I think
		// Scale, translate and rotate ALL MODELS
		glWorld.transformEntity(player);

		// TODO: These transformations are applying to ALL objects. SORT!
		// for (Monster m : monsters) {
		// glWorld.transformEntity(monsters.get(0));
		// }

		// Activate world shader (upload matrices to the uniform variables)
		glWorld.startShader();

		// Clean up
		glWorld.cleanUp();

		glWorld.stopShader();

		exitOnGLError("logicCycle");
	}

	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// Activate world shader (upload matrices to the uniform variables)
		glWorld.startShader();

		player.draw();
		// for (Monster m : monsters) {
		// m.draw();
		// }

		// Deactivates world the shader
		glWorld.stopShader();

		exitOnGLError("renderCycle");
	}

	private void loopCycle() {
		// Update logic
		this.logicCycle();
		// Update rendered frame
		this.renderCycle();

		exitOnGLError("loopCycle");
	}

}