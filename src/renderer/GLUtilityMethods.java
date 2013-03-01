package renderer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import assets.Player;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import exception.RendererException;

public class GLUtilityMethods {

	private GLUtilityMethods() {
	};

	public static int loadShader(String filename, int type) throws RendererException {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String message = "Could not compile shader from file: {0}. The error was: {1}";
			throw new RendererException(MessageFormat.format(message, filename, GL20.glGetShaderInfoLog(shaderID, GL20.GL_INFO_LOG_LENGTH)));
		}

		// TODO: Should this be in a catch block further up the stack?
		exitOnGLError("loadShader");

		return shaderID;
	}

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

	public static void setupOpenGL(int width, int height, String windowTitle) {
		// Setup an OpenGL context with API version 3.2
		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(windowTitle);
			Display.create(pixelFormat, contextAtrributes);

			GL11.glViewport(0, 0, width, height);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Setup an XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);

		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, width, height);

		exitOnGLError("setupOpenGL");
	}

	public static void destroyOpenGL(GLWorld glWorld, Player player) {

		// Clean up world shader
		glWorld.destroy();

		// Clean up all of our models
		// This should probably clean all the shaders attached to a model as
		// well
		player.destroy();

		Display.destroy();
	}

	public static int loadPNGTexture(String filename, int textureUnit) {
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
