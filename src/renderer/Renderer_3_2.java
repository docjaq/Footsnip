package renderer;

import static maths.LinearAlgebra.degreesToRadians;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.loadPNGTexture;
import static renderer.GLUtilityMethods.loadShader;
import static renderer.GLUtilityMethods.setupOpenGL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderer.glmodels.GLTexturedQuad;

public class Renderer_3_2 {
	// Entry point for the application
	public static void main(String[] args) {
		new Renderer_3_2();
	}

	// Setup variables
	private final String WINDOW_TITLE = "The Quad: Moving";
	private final int WIDTH = 800;
	private final int HEIGHT = 600;

	// Quad Texture variables
	private int[] texIds = new int[] { 0, 0 };
	// private int textureSelector = 0;

	// Shader variables
	private int vsId = 0; // vertex shader ID
	private int fsId = 0; // fragment/pixel shader ID
	private int pId = 0; // program ID

	// This is obviously still very hard coded
	private GLTexturedQuad texturedQuad;

	/*
	 * TODO: - Move the texturing stuff into the model
	 * 
	 * - Figure out what the hell do do with the VertexData object
	 * 
	 * - Create a list or something of models - Figure out some way to notify
	 * all the models in some list and update their positions, angles, scales,
	 * etc
	 * 
	 * - Figure out where to put the shader. I'm not entirely sure what it does.
	 */

	private GLWorld glWorld;

	public Renderer_3_2() {
		// Initialize OpenGL (Display)
		setupOpenGL(WIDTH, HEIGHT, WINDOW_TITLE);

		// The vector3f defines the starting camera position. This is not
		// actually modified I don't think...
		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		// game threads
		// update entity stuff

		// start renderer while loop
		Vector3f modelPos = new Vector3f(0, 0, 0);
		Vector3f modelAngle = new Vector3f(0, 0, 0);
		Vector3f modelScale = new Vector3f(0.2f, 0.2f, 0.2f);
		texturedQuad = new GLTexturedQuad(modelPos, modelAngle, modelScale);

		this.setupShaders();
		this.setupTextures();

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

	private void setupTextures() {
		// Reads a PNG, creates an texture, binds it to memory within OPENGL,
		// sets it up (parameterisation-wise) and returns an int 'pointer' which
		// can be used to reference it in OpenGL
		texIds[0] = loadPNGTexture("resources/images/stGrid1.png", GL13.GL_TEXTURE0);
		texIds[1] = loadPNGTexture("resources/images/stGrid2.png", GL13.GL_TEXTURE0);

		exitOnGLError("setupTexture");
	}

	private void setupShaders() {
		// Load the vertex shader
		vsId = loadShader("resources/shaders/moving/vertex.glsl", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fsId = loadShader("resources/shaders/moving/fragment.glsl", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);
		GL20.glLinkProgram(pId);

		// Position information will be attribute 0
		GL20.glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(pId, 1, "in_Color");
		// Texture information will be attribute 2
		GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");

		// Get matrices uniform locations
		glWorld.projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
		glWorld.viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
		glWorld.modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");

		GL20.glValidateProgram(pId);

		exitOnGLError("setupShaders");
	}

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
			texturedQuad.modelPos.x -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			texturedQuad.modelPos.x += posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			texturedQuad.modelPos.y -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			texturedQuad.modelPos.y += posDelta;

		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD))
			Vector3f.add(texturedQuad.modelScale, scaleAddResolution, texturedQuad.modelScale);
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA))
			Vector3f.add(texturedQuad.modelScale, scaleMinusResolution, texturedQuad.modelScale);

		// Just set up a standard rotation for testing
		texturedQuad.modelAngle.z += rotationDelta;

		// -- Update matrices
		// Reset view and model matrices
		glWorld.viewMatrix = new Matrix4f();
		glWorld.modelMatrix = new Matrix4f();

		// Translate camera
		Matrix4f.translate(glWorld.cameraPos, glWorld.viewMatrix, glWorld.viewMatrix);

		// Scale, translate and rotate model
		Matrix4f.scale(texturedQuad.modelScale, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.translate(texturedQuad.modelPos, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(texturedQuad.modelAngle.z), GLWorld.BASIS_Z, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(texturedQuad.modelAngle.y), GLWorld.BASIS_Y, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(texturedQuad.modelAngle.x), GLWorld.BASIS_X, glWorld.modelMatrix, glWorld.modelMatrix);

		// Upload matrices to the uniform variables
		GL20.glUseProgram(pId);

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

	// I don't understand the buffers and Array objects yet. Decide what needs
	// to be in a model and what is shared between models. I have a feeling that
	// each model has a VBA, and a VBO contains lots of VBAs. But I'm not sure,
	// so for simplicity for now, they are all in the GLTexturedQuad class.
	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// This seems to activate the shaders
		GL20.glUseProgram(pId);

		// Bind the texture
		// This is the texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// This is the index to the specific texture to use
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[0]);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(texturedQuad.vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, texturedQuad.vboiId);

		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, texturedQuad.indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

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

	/*
	 * TODO: To move this into the GLUtilityMethods class, I'll have to be able
	 * to globally all of the textures, shaders, etc. So I guess some method
	 * that rips all the stuff out of all the objects (like a garbage
	 * collector), and passes all the lists here
	 */
	private void destroyOpenGL() {
		// Delete the texture
		GL11.glDeleteTextures(texIds[0]);
		GL11.glDeleteTextures(texIds[1]);

		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);

		GL20.glDeleteShader(vsId);
		GL20.glDeleteShader(fsId);
		GL20.glDeleteProgram(pId);

		// Select the VAO
		GL30.glBindVertexArray(texturedQuad.vaoId);

		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(texturedQuad.vboId);

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(texturedQuad.vboiId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(texturedQuad.vaoId);

		exitOnGLError("destroyOpenGL");

		Display.destroy();
	}

}