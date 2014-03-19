package renderer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDepthRange;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferRange;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import math.types.Matrix4;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import assets.entities.Player;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class GLUtilityMethods {

	/** OpenGL 4.2 */
	// private static final int NUM_MIPMAPS = 3;
	private static final boolean ENABLE_MIPMAPPING = true;

	private GLUtilityMethods() {
	};

	public static void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();

		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			if (Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}

	public static void setupOpenGL(int width, int height, String windowTitle, float[] backgroundColor, int projectionUniformBuffer,
			int projectionBlockIndex) {

		// Setup an OpenGL context with API version 3.2
		try {
			PixelFormat pixelFormat = new PixelFormat();
			// withForwardCompatible Forces older versions not to work.
			// Doesn't work on OS X
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(windowTitle);

			// Not necessary to create the canvas like this, except on OS X.
			// Does not guarantee forward compatibility with new OpenGL
			// versions.
			Display.create(pixelFormat, contextAtrributes);

			GL11.glViewport(0, 0, width, height);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Setup an XNA like background color
		// GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
		GL11.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);

		// Map the internal OpenGL coordinate system to the entire screen
		// GL11.glViewport(0, 0, width, height);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CCW);

		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		glDepthFunc(GL_LEQUAL);
		glDepthRange(0, 1);
		glEnable(GL_DEPTH_CLAMP);

		projectionUniformBuffer = glGenBuffers();
		glBindBuffer(GL_UNIFORM_BUFFER, projectionUniformBuffer);
		glBufferData(GL_UNIFORM_BUFFER, 16 * 4, GL_DYNAMIC_DRAW);
		glBindBufferRange(GL_UNIFORM_BUFFER, projectionBlockIndex, projectionUniformBuffer, 0, 16 * 4);
		glBindBuffer(GL_UNIFORM_BUFFER, 0);

		resized(width, height, projectionUniformBuffer);

		exitOnGLError("setupOpenGL");
	}

	public static void resized(int width, int height, int projectionUniformBuffer) {
		GL11.glViewport(0, 0, width, height);
		GL15.glBindBuffer(GL_UNIFORM_BUFFER, projectionUniformBuffer);
		GL15.glBufferSubData(GL_UNIFORM_BUFFER, 0, new Matrix4().clearToPerspectiveDeg(45, width, height, 1, 1000).toBuffer());
		GL15.glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}

	public static void destroyOpenGL(Player player) {
		player.destroy();
		Display.destroy();
	}

	public static int loadPNGTextureAsPicture(String filename, int textureUnit) {
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
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		/** Replace line above with code below for OpenGL 4.2 */
		// GL42.glTexStorage2D(GL11.GL_TEXTURE_2D, NUM_MIPMAPS, GL11.GL_RGBA8,
		// tWidth, tHeight);
		// GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, tWidth, tHeight,
		// GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		// if (ENABLE_MIPMAPPING) {
		// GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		// }

		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		if (ENABLE_MIPMAPPING) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}

		exitOnGLError("loadPNGTexture");

		return texId;
	}

	public static int loadPNGTextureAsData(String filename) {
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
			decoder.decodeFlipped(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		// GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		// MAY NOT BE NECESSARY

		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		exitOnGLError("loadPNGTexture");

		return texId;
	}

	public static int loadArrayTextureAsData(float[][] data) {
		int tWidth = data.length;
		int tHeight = data.length;

		// Change this to
		// http://stackoverflow.com/questions/7070576/get-one-dimensionial-array-from-a-mutlidimensional-array-in-java
		// Float is four bytes
		ByteBuffer buf = ByteBuffer.allocateDirect(tWidth * tHeight * 4);
		buf.order(ByteOrder.nativeOrder());

		FloatBuffer fBuf = buf.asFloatBuffer();
		for (int y = 0; y < tHeight; y++) {
			for (int x = 0; x < tWidth; x++) {
				fBuf.put(data[x][y]);
			}
		}
		fBuf.flip();

		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		// All RGB bytes are aligned to each other and each component is 1 byte
		// GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		// MAY NOT BE NECESSARY

		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, tWidth, tHeight, 0, GL11.GL_RED, GL11.GL_FLOAT, buf);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		return texId;
	}

	public static int bindBufferAs2DTexture(FloatBuffer buf, int dataType, int width, int height) {
		int texId = GL11.glGenTextures();

		// GL33.glClampColor(GL33.GL_CLAMP_READ_COLOR, GL_FALSE);
		// glClampColor(GL_CLAMP_VERTEX_COLOR, GL_FALSE);
		// glClampColor(GL_CLAMP_FRAGMENT_COLOR, GL_FALSE);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, dataType, width, height, 0, dataType, GL11.GL_FLOAT, buf);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		return texId;
	}
}
