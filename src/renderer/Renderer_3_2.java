package renderer;

import static maths.LinearAlgebra.degreesToRadians;
import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.loadShader;
import static renderer.GLUtilityMethods.setupOpenGL;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Renderer_3_2 {
	// Entry point for the application
	public static void main(String[] args) {
		new Renderer_3_2();
	}

	// Setup variables
	private final String WINDOW_TITLE = "The Quad: Moving";
	private final int WIDTH = 800;
	private final int HEIGHT = 600;

	// Quad variables
	private int vaoId = 0;
	private int vboId = 0;
	private int vboiId = 0;
	private int indicesCount = 0;
	private GLVertex[] vertices = null;
	private ByteBuffer verticesByteBuffer = null;
	// Quad Moving variables
	private Vector3f modelPos = null;
	private Vector3f modelAngle = null;
	private Vector3f modelScale = null;
	// Quad Texture variables
	private int[] texIds = new int[] { 0, 0 };
	private int textureSelector = 0;

	// Shader variables
	private int vsId = 0; // vertex shader ID
	private int fsId = 0; // fragment/pixel shader ID
	private int pId = 0; // program ID

	/*
	 * TODO: - Move all quad stuff into a quad object - For now, create and
	 * extend an abstract geometry model type. - Store the modelPos, angle and
	 * scale in the geometry model - Move the texturing stuff into the model -
	 * Figure out what the hell do do with the VertexData object
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

		glWorld = new GLWorld(WIDTH, HEIGHT, new Vector3f(0, 0, 0));

		this.setupQuad();
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
		texIds[0] = this.loadPNGTexture("resources/images/stGrid1.png", GL13.GL_TEXTURE0);
		texIds[1] = this.loadPNGTexture("resources/images/stGrid2.png", GL13.GL_TEXTURE0);

		exitOnGLError("setupTexture");
	}

	private void setupQuad() {
		// We'll define our quad using 4 vertices of the custom 'TexturedVertex'
		// class
		GLVertex v0 = new GLVertex();
		v0.setXYZ(-0.5f, 0.5f, 0);
		v0.setRGB(1, 0, 0);
		v0.setST(0, 0);
		GLVertex v1 = new GLVertex();
		v1.setXYZ(-0.5f, -0.5f, 0);
		v1.setRGB(0, 1, 0);
		v1.setST(0, 1);
		GLVertex v2 = new GLVertex();
		v2.setXYZ(0.5f, -0.5f, 0);
		v2.setRGB(0, 0, 1);
		v2.setST(1, 1);
		GLVertex v3 = new GLVertex();
		v3.setXYZ(0.5f, 0.5f, 0);
		v3.setRGB(1, 1, 1);
		v3.setST(1, 0);

		vertices = new GLVertex[] { v0, v1, v2, v3 };

		// Put each 'Vertex' in one FloatBuffer
		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * GLVertex.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(vertices[i].getElements());
		}
		verticesFloatBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = { 0, 1, 2, 2, 3, 0 };
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, GLVertex.positionElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, GLVertex.colorElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, GLVertex.textureElementCount, GL11.GL_FLOAT, false, GLVertex.stride, GLVertex.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		// Set the default quad rotation, scale and position values
		modelPos = new Vector3f(0, 0, 0);
		modelAngle = new Vector3f(0, 0, 0);
		modelScale = new Vector3f(0.2f, 0.2f, 0.2f);

		exitOnGLError("setupQuad");
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
		// Textute information will be attribute 2
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

		if (Keyboard.isKeyDown(Keyboard.KEY_1) && !Keyboard.isRepeatEvent())
			textureSelector = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_2) && !Keyboard.isRepeatEvent())
			textureSelector = 1;

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			modelPos.x -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			modelPos.x += posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			modelPos.y -= posDelta;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			modelPos.y += posDelta;

		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD))
			Vector3f.add(modelScale, scaleAddResolution, modelScale);
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA))
			Vector3f.add(modelScale, scaleMinusResolution, modelScale);

		// Just set up a standard rotation for testing
		modelAngle.z += rotationDelta;

		// -- Update matrices
		// Reset view and model matrices
		glWorld.viewMatrix = new Matrix4f();
		glWorld.modelMatrix = new Matrix4f();

		// Translate camera
		Matrix4f.translate(glWorld.cameraPos, glWorld.viewMatrix, glWorld.viewMatrix);

		// Scale, translate and rotate model
		Matrix4f.scale(modelScale, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.translate(modelPos, glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1), glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0), glWorld.modelMatrix, glWorld.modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0), glWorld.modelMatrix, glWorld.modelMatrix);

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

	private void renderCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(pId);

		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

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
		GL30.glBindVertexArray(vaoId);

		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);

		exitOnGLError("destroyOpenGL");

		Display.destroy();
	}

	private int loadPNGTexture(String filename, int textureUnit) {
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;

		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);

			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();

			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

		exitOnGLError("loadPNGTexture");

		return texId;
	}
}