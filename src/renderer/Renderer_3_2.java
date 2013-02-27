package renderer;

import static renderer.GLUtilityMethods.destroyOpenGL;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTexturedQuad;
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
		Vector3f modelPos = new Vector3f(1, 0, 0);
		Vector3f modelAngle = new Vector3f(0, 0, 0);
		Vector3f modelScale = new Vector3f(0.1f, 0.1f, 0.1f);
		GLModel model = new GLTexturedQuad(modelPos, modelAngle, modelScale);
		player = new Player(model, "Cunt", 0, new float[] { 1.0f, 0.0f, 0.0f });

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

		// -- Update matrices
		// Reset view and model matrices
		glWorld.clearMatricies();

		// Translate camera
		glWorld.transformCamera();

		// Weird to send a non GL object (entity) to this, rather than the
		// GLModel object... not quite right I think
		// Scale, translate and rotate ALL MODELS
		glWorld.transformEntity(player);

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