package renderer;

import static maths.LinearAlgebra.degreesToRadians;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.setupOpenGL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLModel;
import renderer.glmodels.GLTexturedQuad;
import renderer.glshaders.GLShader;
import renderer.glshaders.GLWorldShader;
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

	private GLShader worldShader;

	// TODO: For now, have a data-structure here of all our entities, etc
	// TODO: Figure out some way to notify all the models in the data structure
	// and update their positions, angles, scales, etc
	private Player player;

	private GLWorld glWorld;

	public Renderer_3_2() throws RendererException {
		// Initialize OpenGL (Display)
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE);

		// The vector3f defines the starting camera position. This is not
		// actually modified I don't think...
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		// game threads
		// update entity stuff

		worldShader = new GLWorldShader(glWorld);
		worldShader.create();
		// this.setupShaders();
		// this.setupTextures();

		createEntities();

		while (!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();

			// Force a maximum FPS of about 60
			Display.sync(0);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		// Destroy OpenGL (Display)
		this.destroyOpenGL();
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
		float rotationDelta = 0.05f;
		float scaleDelta = 0.001f;
		float posDelta = 0.001f;
		Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
		Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);

		// Allows you to hold the key down
		Keyboard.enableRepeatEvents(true);

		/*
		 * if (Keyboard.isKeyDown(Keyboard.KEY_1) && !Keyboard.isRepeatEvent())
		 * textureSelector = 0; if (Keyboard.isKeyDown(Keyboard.KEY_2) &&
		 * !Keyboard.isRepeatEvent()) textureSelector = 1;
		 */

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			player.getModel().modelPos.x -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			player.getModel().modelPos.x += posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			player.getModel().modelPos.y -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			player.getModel().modelPos.y += posDelta;

		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD))
			Vector3f.add(player.getModel().modelScale, scaleAddResolution, player.getModel().modelScale);
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA))
			Vector3f.add(player.getModel().modelScale, scaleMinusResolution, player.getModel().modelScale);

		// Just set up a standard rotation for testing
		player.getModel().modelAngle.z += rotationDelta;

		// -- Update matrices
		// Reset view and model matrices
		glWorld.viewMatrix = new Matrix4f();
		glWorld.modelMatrix = new Matrix4f();

		// Translate camera
		Matrix4f.translate(glWorld.cameraPos, glWorld.viewMatrix, glWorld.viewMatrix);

		// Scale, translate and rotate model
		Matrix4f.scale(player.getModel().modelScale, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.translate(player.getModel().modelPos, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(player.getModel().modelAngle.z), GLWorld.BASIS_Z, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(player.getModel().modelAngle.y), GLWorld.BASIS_Y, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(player.getModel().modelAngle.x), GLWorld.BASIS_X, glWorld.modelMatrix, glWorld.modelMatrix);

		// Upload matrices to the uniform variables
		GL20.glUseProgram(worldShader.programID);

		// Clean up

		glWorld.projectionMatrix.store(glWorld.matrix44Buffer);
		glWorld.matrix44Buffer.flip();
		GL20.glUniformMatrix4(glWorld.projectionMatrixLocation, false, glWorld.matrix44Buffer);
		glWorld.viewMatrix.store(glWorld.matrix44Buffer);
		glWorld.matrix44Buffer.flip();
		GL20.glUniformMatrix4(glWorld.viewMatrixLocation, false, glWorld.matrix44Buffer);
		glWorld.modelMatrix.store(glWorld.matrix44Buffer);
		glWorld.matrix44Buffer.flip();
		GL20.glUniformMatrix4(glWorld.modelMatrixLocation, false, glWorld.matrix44Buffer);

		GL20.glUseProgram(0);

		exitOnGLError("logicCycle");
	}

	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// This seems to activate the shaders
		GL20.glUseProgram(worldShader.programID);

		player.draw();

		// Deactivates the shaders
		GL20.glUseProgram(0);

		exitOnGLError("renderCycle");
	}

	private void loopCycle() {
		// Update logic
		this.logicCycle();
		// Update rendered frame
		this.renderCycle();

		exitOnGLError("loopCycle");
	}

	private void destroyOpenGL() {

		// Delete the core shaders.
		GL20.glUseProgram(0);

		// Clean up world shader
		worldShader.destroy();

		// Clean up all of our models
		// This should probably clean all the shaders attached to a model as
		// well
		player.getModel().cleanUp();

		Display.destroy();
	}

}